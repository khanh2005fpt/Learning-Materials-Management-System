package net.javaguides.lmms.repository;

import net.javaguides.lmms.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name); //Check xem category ton tai hay chua
}
