    package net.javaguides.lmms.service;

    import lombok.RequiredArgsConstructor;
    import net.javaguides.lmms.dto.UploadResponseDTO;
    import net.javaguides.lmms.entity.Book;
    import net.javaguides.lmms.entity.BookPage;
    import net.javaguides.lmms.entity.BookPageDocument;
    import net.javaguides.lmms.entity.Category;
    import net.javaguides.lmms.repository.BookPageRepository;
    import net.javaguides.lmms.repository.BookPageSearchRepository;
    import net.javaguides.lmms.repository.BookRepository;
    import net.javaguides.lmms.repository.CategoryRepository;
    import org.apache.pdfbox.Loader;
    import org.apache.pdfbox.pdmodel.PDDocument;
    import org.apache.pdfbox.text.PDFTextStripper;
    import org.springframework.scheduling.annotation.Async;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    @Service
    @RequiredArgsConstructor
    public class BookService {
        private final BookPageRepository bookPageRepository;
        private final BookRepository bookRepository;
        private final FileStorageService storageService;
        private final BookPageSearchRepository searchRepo;
        private final CategoryRepository categoryRepository;
        // Regex normalize toán học
        private static final Pattern MATH_PATTERN = Pattern.compile("ℳ|σ2|σ|Π|∫|≤|≥");

        private String normalizeMath(String text) {
            Matcher m = MATH_PATTERN.matcher(text);
            return m.replaceAll(match -> switch (match.group()) {
                case "ℳ" -> "\\\\mathcal{M}";
                case "σ2" -> "\\\\sigma\\^2";
                case "σ" -> "\\\\sigma";
                case "Π" -> "\\\\pi";
                case "∫" -> "\\\\int";
                case "≤" -> "<=";
                case "≥" -> ">=";
                default -> match.group();
            });
        }

        public List<Book> getBooksByCategory(String categoryName) {
            return bookRepository.findByCategory_Name(categoryName);
        }

        public void extractFile(Book book, MultipartFile file) throws IOException {
            byte[] pdfBytes = file.getBytes();

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                int totalPages = document.getNumberOfPages();

                List<BookPage> batch = new ArrayList<>();
                int batchSize = 100; // batch insert 100 trang

                for (int i = 1; i <= totalPages; i++) {
                    stripper.setStartPage(i);
                    stripper.setEndPage(i);
                    String text = stripper.getText(document).trim();

                    // Chuẩn hóa ký tự toán học
                    text = normalizeMath(text);

                    // Gộp dòng và xóa khoảng trắng thừa
                    text = text.replaceAll("\\r?\\n", " ")
                            .replaceAll("\\s+", " ");

                    BookPage bookPage = new BookPage();
                    bookPage.setBook(book);
                    bookPage.setPageNumber(i);
                    bookPage.setContent(text);
                    batch.add(bookPage);

                    // Batch insert + index
                    if (batch.size() >= batchSize) {
                        saveAndIndexBatch(batch);
                        batch.clear();
                    }
                }

                // Insert & index batch còn lại
                if (!batch.isEmpty()) {
                    saveAndIndexBatch(batch);
                }

            } catch (IOException e) {
                throw new RuntimeException("Không đọc được file PDF: " + file.getOriginalFilename(), e);
            }
        }

        // Lưu database + index Elasticsearch
        private void saveAndIndexBatch(List<BookPage> pages) {
            List<BookPage> savedPages = bookPageRepository.saveAll(pages);
            indexBatch(savedPages); // bulk index async
        }

        // Bulk index async
        @Async("threadPoolTaskExecutor")
        public void indexBatch(List<BookPage> pages) {
            List<BookPageDocument> docs = pages.stream()
                    .map(p -> new BookPageDocument(
                            p.getBook().getId() + "-" + p.getPageNumber(),
                            p.getBook().getId(),
                            p.getBook().getTitle(),
                            p.getPageNumber(),
                            p.getContent(),
                            p.getBook().getFilepath()
                    ))
                    .toList();
            searchRepo.saveAll(docs);
        }





        @Transactional
        public Book createBook(UploadResponseDTO bookDTO) throws Exception {
            // 2. Tìm hoặc tạo Category
            Category category = categoryRepository
                    .findByName(bookDTO.getCategoryName())
                    .orElseGet(() -> {
                        Category c = new Category();
                        c.setName(bookDTO.getCategoryName());
                        return categoryRepository.save(c);
                    });

            MultipartFile file = bookDTO.getFilePath();
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            if (!file.getContentType().equals("application/pdf")
                    && !file.getContentType().equals("application/octet-stream")) {
                throw new RuntimeException("Only PDF allowed");
            }
            String storedPath = storageService.storeFile(file); //file lưu vào uploads
            Book book = new Book();
            book.setTitle(bookDTO.getTitle());
            book.setAuthor(bookDTO.getAuthor());
            book.setDescription(bookDTO.getDescription());
            book.setFilepath(storedPath);
            book.setCategory(category);

            Book saved = bookRepository.save(book);
            extractFile(saved, file);
            return saved;
        }


        public void deleteBook(Long bookId){
            // 1. Tìm book
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));



            // 2. Xóa tất cả document trong Elasticsearch liên quan tới bookId
            searchRepo.deleteAllByBookId(bookId);

            // 3. Xóa BookPage trong DB
            bookPageRepository.deleteByBookId(bookId);
            // 4. XÓA FILE (KHÔNG ĐƯỢC THROW)
            try {
                storageService.deleteFile(book.getFilepath());
            } catch (Exception e) {
                // log thôi, KHÔNG cho fail
                System.err.println("Không xóa được file: " + e.getMessage());
            }
            // 5. Xóa Book
            bookRepository.delete(book);
        }


        public List<Book> getAllBooks() {
            return bookRepository.findAll();
        }


    }
