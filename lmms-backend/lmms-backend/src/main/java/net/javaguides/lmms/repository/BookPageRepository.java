package net.javaguides.lmms.repository;

import net.javaguides.lmms.entity.BookPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BookPageRepository extends JpaRepository<BookPage, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM BookPage bp WHERE bp.book.id = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);
}
