package net.javaguides.lmms.controller;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.CategoryRequestDTO;
import net.javaguides.lmms.entity.Book;
import net.javaguides.lmms.entity.Category;
import net.javaguides.lmms.repository.BookRepository;
import net.javaguides.lmms.service.BookService;
import net.javaguides.lmms.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;
    private final BookRepository bookRepository;


    @PostMapping
    public Category create(@RequestBody CategoryRequestDTO request) {
        return categoryService.create(request);
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Page<Book> getBooksByCategoryAndPagination(@PathVariable Long id,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookService.getBooksByCategory(id, pageable);
    }

}
