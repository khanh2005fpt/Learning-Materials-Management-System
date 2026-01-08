package net.javaguides.lmms.repository;

import net.javaguides.lmms.entity.BookPageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookPageSearchRepository extends ElasticsearchRepository<BookPageDocument,String> {
    void deleteAllByBookId(Long bookId);

}
