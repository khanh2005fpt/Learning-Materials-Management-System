package net.javaguides.lmms.repository;

import net.javaguides.lmms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
