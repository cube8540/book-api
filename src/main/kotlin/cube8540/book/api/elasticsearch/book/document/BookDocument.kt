package cube8540.book.api.elasticsearch.book.document

import java.time.LocalDate
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = "books")
data class BookDocument(
    @Id
    @Field(name = isbnFieldName, type = FieldType.Keyword)
    val isbn: String,

    @Field(name = titleFieldName, type = FieldType.Text)
    val title: String,

    @Field(name = publishDateFieldName, type = FieldType.Date, format = [DateFormat.date])
    val publishDate: LocalDate,

    @Field(name = publisherCodeFieldName, type = FieldType.Keyword)
    val publisherCode: String
) {
    companion object {
        const val isbnFieldName = "isbn"
        const val titleFieldName = "title"
        const val publishDateFieldName = "publish_date"
        const val publisherCodeFieldName = "publisher_code"
    }
}