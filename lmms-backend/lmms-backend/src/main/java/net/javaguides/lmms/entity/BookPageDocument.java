package net.javaguides.lmms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "book_pages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookPageDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long bookId;

    @Field(type = FieldType.Text)
    private String bookTitle;

    @Field(type = FieldType.Integer)
    private int pageNumber;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    @Field(type = FieldType.Keyword)
    private String pdfPath;
}
