package net.javaguides.lmms.service;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.UploadResponseDTO;
import net.javaguides.lmms.entity.Book;
import net.javaguides.lmms.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final FileStorageService storageService;
    public Book createBook(UploadResponseDTO bookDTO) throws Exception {
        MultipartFile file = bookDTO.getFilePath();
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!file.getContentType().equals("application/pdf")
                && !file.getContentType().equals("application/octet-stream")) {
            throw new RuntimeException("Only PDF allowed");
        }
        String storedPath = storageService.storeFile(file);
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setDescription(bookDTO.getDescription());
        book.setFilepath(storedPath);
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

}
