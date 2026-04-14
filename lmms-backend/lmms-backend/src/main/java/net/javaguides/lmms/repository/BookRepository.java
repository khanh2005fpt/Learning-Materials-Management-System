package net.javaguides.lmms.repository;

import net.javaguides.lmms.entity.Book;
import net.javaguides.lmms.entity.BookPageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByCategory_Id(Long categoryId, Pageable pageable);
    boolean existsByTitle(String title);
    boolean existsByDescription(String description);
}
