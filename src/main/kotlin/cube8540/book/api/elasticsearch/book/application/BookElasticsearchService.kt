package cube8540.book.api.elasticsearch.book.application

import cube8540.book.api.elasticsearch.book.document.BookDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BookElasticsearchService {
    fun search(condition: BookElasticsearchCondition, pageable: Pageable): Page<BookDocument>
}