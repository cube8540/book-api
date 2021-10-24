package cube8540.book.api.book.infra

import cube8540.book.api.book.domain.BookInvalidException
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.elasticsearch.book.application.BookElasticsearchCondition
import cube8540.book.api.elasticsearch.book.application.BookElasticsearchService
import cube8540.book.api.elasticsearch.book.application.DocumentPageInvalidException
import io.github.cube8540.validator.core.ValidationError
import java.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

data class BookExternalSearchRequest(
    val title: String,
    val publishFrom: LocalDate?,
    val publishTo: LocalDate?,
    val publisherCode: String?
)

interface BookExternalSearchExchanger {
    fun search(searchRequest: BookExternalSearchRequest, pageable: Pageable): Page<Isbn>
}

@Service
class BookElasticsearchExchanger @Autowired constructor(
    private val elasticsearchService: BookElasticsearchService
): BookExternalSearchExchanger {

    companion object {
        const val defaultPagePropertyName = "page"
    }

    var pagePropertyName: String = defaultPagePropertyName

    override fun search(searchRequest: BookExternalSearchRequest, pageable: Pageable): Page<Isbn> {
        val elasticsearchRequest = BookElasticsearchCondition(
            title = searchRequest.title,
            publishFrom = searchRequest.publishFrom,
            publishTo = searchRequest.publishTo,
            publisherCode = searchRequest.publisherCode
        )
        try {
            return elasticsearchService.search(elasticsearchRequest, pageable).map { Isbn(it.isbn) }
        } catch (e: DocumentPageInvalidException) {
            throw BookInvalidException.instance(listOf(ValidationError(pagePropertyName, e.message)))
        }
    }
}