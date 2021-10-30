package cube8540.book.api.book.application

import cube8540.book.api.book.domain.*
import cube8540.book.api.book.infra.BookPostRequestBasedInitializer
import cube8540.book.api.book.repository.BookQueryCondition
import cube8540.book.api.book.repository.BookRepository
import cube8540.book.api.book.repository.PublisherRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationBookService constructor(
    private val bookRepository: BookRepository,
    private val publisherRepository: PublisherRepository
): BookPageSearchService, BookRegisterService, BookDetailsService {

    @set:Autowired
    lateinit var validatorFactory: BookValidatorFactory

    @Transactional(readOnly = true)
    override fun lookupBooks(condition: BookLookupCondition, pageable: Pageable): Page<BookDetail> {
        val queryCondition = BookQueryCondition(
            publishFrom = condition.publishFrom,
            publishTo = condition.publishTo,
            publisherCode = condition.publisherCode,
            seriesCode = condition.seriesCode,
            seriesIsbn = condition.seriesIsbn
        )
        val pageRequest = PageRequest.of(pageable.pageNumber, pageable.pageSize)
            .withSort(Sort.by(condition.direction, QBook.book.publishDate.metadata.name))

        val page = bookRepository.findPageByCondition(queryCondition, pageRequest)
        return page.map { BookDetail.withoutCollection(it) }
    }

    @Transactional
    override fun upsertBook(upsertRequests: List<BookPostRequest>): BookPostResult {
        val requestBooks = upsertRequests.map { makeBook(it) }
        val existsBooks = bookRepository.findDetailsByIsbn(requestBooks.map { it.isbn })

        val entities = ArrayList<Book>()
        val failedBooks = ArrayList<BookPostErrorReason>()
        requestBooks.forEach { requestBook ->
            val validationResult = requestBook.validationResult(validatorFactory)
            if (validationResult.hasError()) {
                failedBooks.add(BookPostErrorReason(requestBook.isbn.value, validationResult.errors))
            } else {
                existsBooks.find { exists -> exists == requestBook }
                    ?.apply { this.mergeBook(requestBook) }
                    ?.let { entities.add(it) }
                    ?: entities.add(requestBook)
            }
        }
        bookRepository.saveAll(entities)
        return BookPostResult(entities.map { it.isbn.value }, failedBooks)
    }

    @Transactional(readOnly = true)
    override fun getBookDetails(isbn: Isbn): BookDetail? =
        bookRepository.findDetailsByIsbn(isbn)?.let { BookDetail.of(it) }

    @Transactional(readOnly = true)
    override fun getSeriesList(series: Series): List<BookDetail> {
        if (series.isbn == null && series.code == null) {
            return emptyList()
        }
        return bookRepository.findSeries(series).map { BookDetail.of(it) }
    }

    private fun makeBook(upsertRequest: BookPostRequest): Book {
        val book = Book(
            isbn = Isbn(upsertRequest.isbn),
            title = upsertRequest.title,
            publishDate = upsertRequest.publishDate,
            publisher = publisherRepository.getById(upsertRequest.publisherCode)
        )
        BookPostRequestBasedInitializer(upsertRequest).initializingBook(book)
        return book
    }
}