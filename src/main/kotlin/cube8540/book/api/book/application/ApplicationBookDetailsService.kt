package cube8540.book.api.book.application

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.BookValidatorFactory
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.infra.BookPostRequestBasedInitializer
import cube8540.book.api.book.repository.BookRepository
import cube8540.book.api.book.repository.PublisherRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationBookDetailsService constructor(
    private val bookRepository: BookRepository,
    private val publisherRepository: PublisherRepository
): BookRegisterService {

    @set:Autowired
    lateinit var validatorFactory: BookValidatorFactory

    @Transactional
    override fun upsertBook(upsertRequests: List<BookPostRequest>) {
        val requestBooks = upsertRequests.map { makeBook(it) }
        val existsBooks = bookRepository.findDetailsByIsbn(requestBooks.map { it.isbn })

        val entities = ArrayList<Book>()
        requestBooks.forEach { requestBook ->
            existsBooks.find { exists -> exists == requestBook }
                ?.apply { this.mergeBook(requestBook) }
                ?.let { entities.add(it) }
                ?: entities.add(requestBook)
        }

        bookRepository.saveAll(entities)
    }

    private fun makeBook(upsertRequest: BookPostRequest): Book {
        val book = Book(
            isbn = Isbn(upsertRequest.isbn),
            title = upsertRequest.title,
            publishDate = upsertRequest.publishDate,
            publisher = publisherRepository.getById(upsertRequest.publisherCode)
        )
        BookPostRequestBasedInitializer(upsertRequest).initializingBook(book)
        book.isValid(validatorFactory)
        return book
    }
}