package net.javaguides.lmms.controller;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.UploadResponseDTO;
import net.javaguides.lmms.entity.Book;
import net.javaguides.lmms.entity.Category;
import net.javaguides.lmms.repository.BookRepository;
import net.javaguides.lmms.service.BookService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BookRepository bookRepository;
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadBook(@PathVariable String filename) throws IOException {
        Path path = Paths.get("uploads").resolve(filename).normalize();
        Resource resource = new UrlResource(path.toUri());

        if(!resource.exists()){
            throw new FileNotFoundException(filename);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF) //Báo cáo là file pdf
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + resource.getFile().getName() + "\"") //Download file thay vì mở
                .body(resource); //Nội dung file
    }

    @PostMapping("/upload")
    public Book uploadBook(@RequestParam("file") MultipartFile file,
                           @RequestParam(required = false) String author,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) String categoryName) throws Exception {
        return bookService.createBook(new UploadResponseDTO(file, author, title, description, categoryName));
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

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody UploadResponseDTO updatedBook){
       return bookService.updateBook(id, updatedBook);
    }
}
