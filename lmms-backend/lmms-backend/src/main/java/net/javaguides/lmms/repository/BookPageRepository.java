package net.javaguides.lmms.repository;

import net.javaguides.lmms.entity.BookPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BookPageRepository extends JpaRepository<BookPage, Long> {
    @Transactional
    void deleteByBookId(Long bookId);

    // Find all pages for a given book id (used for reindexing/sync)
    java.util.List<BookPage> findByBook_Id(Long bookId);
}
