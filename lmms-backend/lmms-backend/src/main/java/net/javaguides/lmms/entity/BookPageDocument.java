package net.javaguides.lmms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "book_pages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookPageDocument {

    @Id
    private String id;

    private Long bookId;
    private String bookTitle;
    private int pageNumber;

    private String content;
}
