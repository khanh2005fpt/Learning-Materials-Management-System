package net.javaguides.lmms.controller;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.UploadResponseDTO;
import net.javaguides.lmms.entity.Book;
import net.javaguides.lmms.repository.BookRepository;
import net.javaguides.lmms.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BookRepository bookRepository;
    @PostMapping("/upload")
    public Book uploadBook(@RequestParam("file") MultipartFile file,
                           @RequestParam(required = false) String author,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String description) throws Exception {
        return bookService.createBook(new UploadResponseDTO(file, author, title, description));
    }

    @GetMapping
    public List<Book> list() {
        return bookRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        return bookRepository.findById(id).map(book -> {
            try {

                bookService.deleteBook(id);

                return ResponseEntity.ok("Book deleted successfully");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error deleting book: " + e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }


}
