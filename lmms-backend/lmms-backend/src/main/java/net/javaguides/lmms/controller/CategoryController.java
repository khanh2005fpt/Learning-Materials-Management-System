package net.javaguides.lmms.controller;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.CategoryRequestDTO;
import net.javaguides.lmms.entity.Book;
import net.javaguides.lmms.entity.Category;
import net.javaguides.lmms.service.BookService;
import net.javaguides.lmms.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;


    @PostMapping
    public Category create(@RequestBody CategoryRequestDTO request) {
        return categoryService.create(request);
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public List<Book> getBooksByCategory(@PathVariable Long id) {
        return bookService.getBooksByCategory(id);
    }


}
