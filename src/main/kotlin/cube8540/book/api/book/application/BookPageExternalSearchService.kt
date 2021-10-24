package cube8540.book.api.book.application

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.BookInvalidException
import cube8540.book.api.book.infra.BookExternalSearchExchanger
import cube8540.book.api.book.infra.BookExternalSearchRequest
import cube8540.book.api.book.repository.BookRepository
import io.github.cube8540.validator.core.ValidationError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookPageExternalSearchService @Autowired constructor(
    private val externalSearchExchanger: BookExternalSearchExchanger,
    private val bookRepository: BookRepository
): BookPageSearchService {

    var titlePropertyName = Book::title.name
    var titleNullErrorMessage = "title must not be null"

    @Transactional
    override fun lookupBooks(condition: BookLookupCondition, pageable: Pageable): Page<BookDetails> {
        val title = condition.title
            ?: throw BookInvalidException.instance(listOf(ValidationError(Book::title.name, "title must not be null")))

        val searchRequest = BookExternalSearchRequest(title = title,
            publishFrom = condition.publishFrom,
            publishTo = condition.publishTo,
            publisherCode = condition.publisherCode)
        val searchResults = externalSearchExchanger.search(searchRequest, pageable)
        val books = bookRepository.findDetailsByIsbn(searchResults.content)
            .sortedBy { searchResults.content.indexOf(it.isbn) }
            .map { BookDetails.of(it) }
        return PageImpl(books, searchResults.pageable, searchResults.totalElements)
    }
}