package cube8540.book.api.elasticsearch.book.application

import cube8540.book.api.elasticsearch.book.document.BookDocument
import java.time.LocalDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

data class BookElasticsearchCondition(
    var publishFrom: LocalDate? = null,

    var publishTo: LocalDate? = null,

    var publisherCode: String? = null,

    var title: String
)

interface BookElasticsearchService {
    fun search(condition: BookElasticsearchCondition, pageable: Pageable): Page<BookDocument>
}