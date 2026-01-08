package net.javaguides.lmms.service;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.CategoryRequestDTO;
import net.javaguides.lmms.entity.Category;
import net.javaguides.lmms.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    //Tạo mới category
    public Category create(CategoryRequestDTO req) {
        if (categoryRepository.existsByName(req.getName())) {
            throw new RuntimeException("Category already exists");
        }

        Category c = new Category();
        c.setName(req.getName());

        return categoryRepository.save(c);
    }

    //Lấy category theo ID
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }


    //Xóa category
    public void delete(Long id) {
        Category c = getById(id);

        categoryRepository.delete(c);
    }
}

