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
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
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
        private final EmbeddingService embeddingService;
        private final CategoryRepository categoryRepository;
        // Regex normalize toán học
        private static final Pattern MATH_PATTERN = Pattern.compile("ℳ|σ2|σ|Π|∫|≤|≥");
        private static final int CHUNK_MAX_LENGTH = 1200; //
        private static final int CHUNK_MIN_LENGTH = 5;
        private static final Pattern MATH_LINE_PATTERN =
                Pattern.compile(".*[=∫σΣΠ^_≤≥].*");

        //Hàm tách thành chunk
        private List<String> splitToChunks(String text) {
            List<String> chunks = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();

            String[] lines = text.split("\\n|(?<=\\.)\\s+");

            for (String line : lines) {
                boolean isMathLine = MATH_LINE_PATTERN.matcher(line).matches();

                // Nếu là dòng toán → luôn giữ chung buffer
                buffer.append(line).append(" ");

                if (!isMathLine && buffer.length() >= CHUNK_MAX_LENGTH) {
                    chunks.add(buffer.toString().trim());
                    buffer.setLength(0);
                }
            }

            if (buffer.length() >= CHUNK_MIN_LENGTH) {
                chunks.add(buffer.toString().trim());
            }

            return chunks;
        }


        private String normalizeMath(String plainText) {
            if (plainText == null) {
                return null;
            }
            return plainText.replace("\\", "\\textbackslash{}")
                    .replace("&", "\\&")
                    .replace("%", "\\%")
                    .replace("$", "\\$")
                    .replace("#", "\\#")
                    .replace("_", "\\_")
                    .replace("{", "\\{")
                    .replace("}", "\\}")
                    .replace("^", "\\textasciicircum{}")
                    .replace("~", "\\textasciitilde{}");
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

                    List<String> chunks = splitToChunks(text);

                    for (String chunk : chunks) {
                        BookPage page = new BookPage();
                        page.setBook(book);
                        page.setPageNumber(i);
                        page.setContent(chunk);
                        batch.add(page);
                    }

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

        // Bulk index async với embeddings
        @Async("threadPoolTaskExecutor")
        public void indexBatch(List<BookPage> pages) {
                List<BookPageDocument> docs = new ArrayList<>();
            int chunkIndex = 0;

            for (BookPage page : pages) {
                        float[] embedding = embeddingService.generateContentEmbedding(page.getContent());

                        String id = page.getBook().getId()
                        + "-" + page.getPageNumber()
                        + "-" + chunkIndex++;

                        BookPageDocument bookPageDocument = new BookPageDocument(
                                id, // unique id
                                page.getBook().getId(),
                                page.getBook().getTitle(),
                                page.getPageNumber(),
                                page.getContent(), // lưu chunk
                                page.getBook().getFilepath(),
                                embedding
                        );
                        docs.add(bookPageDocument);

                }
                if(!docs.isEmpty()) {
                    searchRepo.saveAll(docs);
                }
            System.out.println("Indexed " + docs.size() + " chunks into Elasticsearch.");
        }





        @Transactional
        public Book createBook(UploadResponseDTO bookDTO) throws Exception {
            // 1. Validate DTO null
            if (bookDTO == null) {
                throw new RuntimeException("Dữ liệu quyển sách không được null");
            }

            // 2. Validate title
            String title = bookDTO.getTitle();
            if (title == null || title.trim().isEmpty()) {
                throw new RuntimeException("Title không được để trống");
            }
            if (title.length() > 255) {
                throw new RuntimeException("Title quá dài");
            }
            if (bookRepository.existsByTitle(title)) {
                throw new RuntimeException("Title đã tồn tại");
            }

            // 3. Validate author
            String author = bookDTO.getAuthor();
            if (author == null || author.trim().isEmpty()) {
                throw new RuntimeException("Author không được để trống");
            }
            // 4. Validate description
            String description = bookDTO.getDescription();
            if (description == null || description.trim().isEmpty()) {
                throw new RuntimeException("Description không được để trống");
            }
            if (description.length() > 1000) {
                throw new RuntimeException("Description quá dài");
            }
            if (bookRepository.existsByDescription(description)) {
                throw new RuntimeException("Description đã tồn tại");
            }



            // 5. Tìm hoặc tạo Category
            Category category = categoryRepository
                    .findByName(bookDTO.getCategoryName())
                    .orElseGet(() -> {
                        Category c = new Category();
                        c.setName(bookDTO.getCategoryName());
                        return categoryRepository.save(c);
                    });

            // 6. Validate file
            MultipartFile file = bookDTO.getFilePath();
            if (file.isEmpty()) {
                throw new RuntimeException("File đang trống");
            }

            if (!file.getContentType().equals("application/pdf")
                    && !file.getContentType().equals("application/octet-stream")) {
                throw new RuntimeException("Chỉ được file pdf");
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

        public Page<Book> getBooksByCategory(Long categoryid, Pageable pageable) {
            return bookRepository.findByCategory_Id(categoryid, pageable);
        }


        public Book updateBook(Long bookid, UploadResponseDTO updatedBook){


            Book book = bookRepository.findById(bookid).orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
            if (updatedBook.getCategoryName() != null && !updatedBook.getCategoryName().isBlank()) {

                Category category = categoryRepository
                        .findByName(updatedBook.getCategoryName())
                        .orElseGet(() -> {
                            Category c = new Category();
                            c.setName(updatedBook.getCategoryName());
                            return categoryRepository.save(c);
                        });

                book.setCategory(category);
            }
            if(updatedBook.getTitle() != null && !updatedBook.getTitle().isBlank()) {
                book.setTitle(updatedBook.getTitle());
            }
            if(updatedBook.getAuthor() != null && !updatedBook.getAuthor().isBlank()) {
                book.setAuthor(updatedBook.getAuthor());

            }
            if(updatedBook.getDescription() != null && !updatedBook.getDescription().isBlank()) {
                book.setDescription(updatedBook.getDescription());
            }

            Book saved = bookRepository.save(book);
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
