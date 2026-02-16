package net.javaguides.lmms.repository;

import net.javaguides.lmms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByCategory_Id(Long categoryId);
}
