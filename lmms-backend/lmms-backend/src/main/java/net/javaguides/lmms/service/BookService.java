    package net.javaguides.lmms.service;

    import lombok.RequiredArgsConstructor;
    import net.javaguides.lmms.dto.UploadResponseDTO;
    import net.javaguides.lmms.entity.Book;
    import net.javaguides.lmms.entity.BookPage;
    import net.javaguides.lmms.entity.BookPageDocument;
    import net.javaguides.lmms.repository.BookPageRepository;
    import net.javaguides.lmms.repository.BookPageSearchRepository;
    import net.javaguides.lmms.repository.BookRepository;
    import org.apache.pdfbox.Loader;
    import org.apache.pdfbox.pdmodel.PDDocument;
    import org.apache.pdfbox.text.PDFTextStripper;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class BookService {
        private final BookPageRepository bookPageRepository;
        private final BookRepository bookRepository;
        private final FileStorageService storageService;
        private final BookPageSearchRepository searchRepo;

        public void index(BookPage page) {

            BookPageDocument doc = new BookPageDocument(
                    page.getBook().getId() + "-" + page.getPageNumber(),
                    page.getBook().getId(),
                    page.getBook().getTitle(),
                    page.getPageNumber(),
                    page.getContent()
            );

            searchRepo.save(doc);
        }

        public void extractFile(Book book, MultipartFile file) throws IOException {
            byte[] pdfBytes = file.getBytes();
            try(PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                int totalPages = document.getNumberOfPages();
                for(int i = 1; i <= totalPages; i++) {
                    stripper.setStartPage(i);
                    stripper.setEndPage(i);
                    String text = stripper.getText(document).trim();

                    BookPage bookPage = new BookPage();
                    bookPage.setBook(book);
                    bookPage.setPageNumber(i);
                    bookPage.setContent(text);
                    BookPage saved = bookPageRepository.save(bookPage);
                    index(saved);
                }
            } catch (IOException e) {
                throw new RuntimeException("Không đọc được file PDF: " + file.getOriginalFilename(), e);
            }
        }

        @Transactional
        public Book createBook(UploadResponseDTO bookDTO) throws Exception {
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

            Book saved = bookRepository.save(book);
            extractFile(saved, file);
            return saved;
        }


        public void deleteBook(Long bookId){
            // 1. Tìm book
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));



            // 2. Xóa Elasticsearch index theo bookId
            searchRepo.deleteByBookId(bookId);

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
