package cube8540.book.api.book.application

import cube8540.book.api.book.domain.*
import cube8540.book.api.book.repository.BookRepository
import cube8540.book.api.book.repository.PublisherRepository
import cube8540.book.api.createValidationResults
import io.github.cube8540.validator.core.ValidationError
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.internal.IgnoringFieldsComparator
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

internal class ApplicationBookDetailsServiceTest {

    private val bookRepository: BookRepository = mockk(relaxed = true)
    private val publisherRepository: PublisherRepository = mockk(relaxed = true)

    private val validatorFactory: BookValidatorFactory = mockk(relaxed = true)

    private val service = ApplicationBookDetailsService(bookRepository, publisherRepository)

    init {
        service.validatorFactory = validatorFactory
    }

    @Nested
    inner class LookupBookTest {

        @Test
        fun `lookup book details`() {
            val condition = createBookLookupCondition(direction = Sort.Direction.ASC)
            val pageRequest = PageRequest.of(0, 10)

            val books = listOf(createBook(isbn = "isbn0000", publisher = createPublisher()),
                createBook(isbn = "isbn0001", publisher = createPublisher()),
                createBook(isbn = "isbn0002", publisher = createPublisher()))
            val expectedCondition = createBookQueryCondition()
            val expectedPageRequest = pageRequest.withSort(Sort.by(Sort.Direction.ASC, QBook.book.publishDate.metadata.name))

            every { bookRepository.findPageByCondition(expectedCondition, expectedPageRequest) } returns PageImpl(books, expectedPageRequest, books.size.toLong())

            val results = service.lookupBooks(condition, pageRequest)
            assertThat(results.totalElements).isEqualTo(books.size.toLong())
            assertThat(results.pageable).isEqualTo(expectedPageRequest)
            assertThat(results.content)
                .usingRecursiveFieldByFieldElementComparator()
                .usingElementComparatorIgnoringFields(*bookDetailsAssertIgnoreFields)
                .usingComparatorForElementFieldsWithNames(IgnoringFieldsComparator(*publisherDetailsAssertIgnoreFields), BookDetails::publisher.name)
                .containsExactly(createBookDetails(isbn = "isbn0000"), createBookDetails(isbn = "isbn0001"), createBookDetails(isbn = "isbn0002"))
        }
    }

    @Nested
    inner class UpsertBookTest {
        @Test
        fun `request data is empty`() {
            val requestList: List<BookPostRequest> = emptyList()

            assertThatCode { service.upsertBook(requestList) }.doesNotThrowAnyException()
        }

        @Test
        fun `book data invalid`() {
            val publisherReferenceMock: Publisher = mockk(relaxed = true)
            val requestList: List<BookPostRequest> = listOf(createBookPostRequest(isbn = "isbn000000"))

            every { publisherRepository.getById(defaultPublisherCode) } returns publisherReferenceMock
            every { validatorFactory.createValidator(createBook(isbn = "isbn000000")) } returns mockk() {
                every { result } returns createValidationResults(
                    ValidationError("isbn", "000000")
                )
            }

            val result = service.upsertBook(requestList)
            assertThat(result.failedBooks).containsExactly(
                BookPostErrorReason(isbn = "isbn000000", listOf(ValidationError("isbn", "000000")))
            )
        }

        @Test
        fun `insert request book`() {
            val requestList: List<BookPostRequest> = listOf(
                createBookPostRequest(isbn = "isbn000001"),
                createBookPostRequest(isbn = "isbn000002"),
                createBookPostRequest(isbn = "isbn000003")
            )
            val publisherReferenceMock: Publisher = mockk(relaxed = true)
            val insertedBookCaptor = slot<Iterable<Book>>()

            every { bookRepository.findDetailsByIsbn(anyList()) } returns emptyList()
            every { bookRepository.saveAll(capture(insertedBookCaptor)) } returnsArgument 0
            every { publisherRepository.getById(defaultPublisherCode) } returns publisherReferenceMock
            every { validatorFactory.createValidator(any()) } returns mockk {
                every { result } returns createValidationResults()
            }

            val result = service.upsertBook(requestList)
            assertThat(insertedBookCaptor.captured)
                .usingElementComparatorIgnoringFields(*bookAssertIgnoreFields)
                .contains(createBook(isbn = "isbn000001", publisher = publisherReferenceMock),
                    createBook(isbn = "isbn000002", publisher = publisherReferenceMock),
                    createBook(isbn = "isbn000003", publisher = publisherReferenceMock))
            assertThat(result.successBooks).containsExactly("isbn000001", "isbn000002", "isbn000003")
        }

        @Test
        fun `update request book`() {
            val requestList: List<BookPostRequest> = listOf(
                createBookPostRequest(isbn = "isbn000001", title = "newTitle0001"),
                createBookPostRequest(isbn = "isbn000002", title = "newTitle0002"),
                createBookPostRequest(isbn = "isbn000003", title = "newTitle0003")
            )
            val existsBooks = listOf(
                createBook(isbn = "isbn000001", title = "beforeTitle0001", newObject = false),
                createBook(isbn = "isbn000002", title = "beforeTitle0002", newObject = false),
                createBook(isbn = "isbn000003", title = "beforeTitle0003", newObject = false)
            )
            val updatedBookCaptor = slot<Iterable<Book>>()

            every { bookRepository.findDetailsByIsbn(existsBooks.map { it.isbn }) } returns existsBooks
            every { bookRepository.saveAll(capture(updatedBookCaptor)) } returnsArgument 0
            every { validatorFactory.createValidator(any()) } returns mockk {
                every { result } returns createValidationResults()
            }

            val result = service.upsertBook(requestList)
            assertThat(updatedBookCaptor.captured)
                .usingElementComparatorIgnoringFields(*bookAssertIgnoreFields)
                .contains(createBook(isbn = "isbn000001", title = "newTitle0001", newObject = false),
                    createBook(isbn = "isbn000002", title = "newTitle0002", newObject = false),
                    createBook(isbn = "isbn000003", title = "newTitle0003", newObject = false))
            assertThat(result.successBooks).containsExactly("isbn000001", "isbn000002", "isbn000003")
        }

        @Test
        fun `insert and update`() {
            val requestList: List<BookPostRequest> = listOf(
                createBookPostRequest(isbn = "isbn000001", title = "newTitle0001"),
                createBookPostRequest(isbn = "isbn000002", title = "newTitle0002"),
                createBookPostRequest(isbn = "isbn000003", title = "newTitle0003")
            )
            val publisherReferenceMock: Publisher = mockk(relaxed = true)
            val existsBooks = listOf(createBook(isbn = "isbn000002", title = "beforeTitle0002", newObject = false))
            val upsertBookCaptor = slot<Iterable<Book>>()

            every { bookRepository.findDetailsByIsbn(requestList.map { Isbn(it.isbn) }) } returns existsBooks
            every { bookRepository.saveAll(capture(upsertBookCaptor)) } returnsArgument 0
            every { publisherRepository.getById(defaultPublisherCode) } returns publisherReferenceMock
            every { validatorFactory.createValidator(any()) } returns mockk {
                every { result } returns createValidationResults()
            }

            val result = service.upsertBook(requestList)
            assertThat(upsertBookCaptor.captured)
                .usingElementComparatorIgnoringFields(*bookAssertIgnoreFields)
                .contains(createBook(isbn = "isbn000001", title = "newTitle0001", publisher = publisherReferenceMock),
                    createBook(isbn = "isbn000002", title = "newTitle0002", newObject = false),
                    createBook(isbn = "isbn000003", title = "newTitle0003", publisher = publisherReferenceMock))
            assertThat(result.successBooks).containsExactly("isbn000001", "isbn000002", "isbn000003")
        }
    }
}