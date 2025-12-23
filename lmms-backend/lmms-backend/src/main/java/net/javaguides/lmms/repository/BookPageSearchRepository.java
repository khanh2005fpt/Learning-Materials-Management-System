package net.javaguides.lmms.repository;

import net.javaguides.lmms.entity.BookPageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookPageSearchRepository extends ElasticsearchRepository<BookPageDocument,String> {
    void deleteByBookId(Long bookId);
    void deleteAllByBookId(Long bookId);

}
