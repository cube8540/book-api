package cube8540.book.api.book.application

import cube8540.book.api.book.domain.*
import cube8540.book.api.book.repository.BookRepository
import io.github.cube8540.validator.core.ValidationResult
import io.github.cube8540.validator.core.Validator
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList

internal class DefaultBookRegisterServiceTest {

    private val bookRepository: BookRepository = mockk(relaxed = true)
    private val validatorFactory: BookValidatorFactory = mockk(relaxed = true)

    private val validator: Validator<Book> = mockk(relaxed = true)
    private val validationResult: ValidationResult = mockk(relaxed = true)

    private val service = DefaultBookRegisterService(bookRepository, validatorFactory)

    init {
        every { validator.result } returns validationResult
    }

    @Nested
    inner class UpsertBookTest {
        @Test
        fun `book data invalid`() {
            val requestList: List<BookPostRequest> = listOf(createBookPostRequest(isbn = "isbn000001"))
            val validationCaptor = slot<Book>()

            every { validatorFactory.createValidator(capture(validationCaptor)) } returns validator
            every { validationResult.hasErrorThrows(any()) } throws BookTestException()

            val thrown = catchThrowable { service.upsertBook(requestList) }
            assertThat(thrown).isInstanceOf(BookTestException::class.java)
            assertThat(validationCaptor.captured)
                .isEqualToIgnoringGivenFields(createBook(isbn = "isbn000001"), *bookAssertIgnoreFields)
        }

        @Test
        fun `insert request book`() {
            val requestList: List<BookPostRequest> = listOf(
                createBookPostRequest(isbn = "isbn000001"),
                createBookPostRequest(isbn = "isbn000002"),
                createBookPostRequest(isbn = "isbn000003")
            )
            val insertedBookCaptor = slot<Iterable<Book>>()

            every { bookRepository.findDetailsByIsbn(anyList()) } returns emptyList()
            every { bookRepository.saveAll(capture(insertedBookCaptor)) } returnsArgument 0
            every { validatorFactory.createValidator(any()) } returns validator
            every { validationResult.hasErrorThrows(any()) } just Runs

            service.upsertBook(requestList)
            assertThat(insertedBookCaptor.captured)
                .usingElementComparatorIgnoringFields(*bookAssertIgnoreFields)
                .contains(createBook(isbn = "isbn000001"), createBook(isbn = "isbn000002"), createBook(isbn = "isbn000003"))
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
            every { validatorFactory.createValidator(any()) } returns validator
            every { validationResult.hasErrorThrows(any()) } just Runs

            service.upsertBook(requestList)
            assertThat(updatedBookCaptor.captured)
                .usingElementComparatorIgnoringFields(*bookAssertIgnoreFields)
                .contains(createBook(isbn = "isbn000001", title = "newTitle0001", newObject = false),
                    createBook(isbn = "isbn000002", title = "newTitle0002", newObject = false),
                    createBook(isbn = "isbn000003", title = "newTitle0003", newObject = false))
        }

        @Test
        fun `insert and update`() {
            val requestList: List<BookPostRequest> = listOf(
                createBookPostRequest(isbn = "isbn000001", title = "newTitle0001"),
                createBookPostRequest(isbn = "isbn000002", title = "newTitle0002"),
                createBookPostRequest(isbn = "isbn000003", title = "newTitle0003")
            )
            val existsBooks = listOf(createBook(isbn = "isbn000002", title = "beforeTitle0002", newObject = false))
            val upsertBookCaptor = slot<Iterable<Book>>()

            every { bookRepository.findDetailsByIsbn(requestList.map { Isbn(it.isbn) }) } returns existsBooks
            every { bookRepository.saveAll(capture(upsertBookCaptor)) } returnsArgument 0
            every { validatorFactory.createValidator(any()) } returns validator
            every { validationResult.hasErrorThrows(any()) } just Runs

            service.upsertBook(requestList)
            assertThat(upsertBookCaptor.captured)
                .usingElementComparatorIgnoringFields(*bookAssertIgnoreFields)
                .contains(createBook(isbn = "isbn000001", title = "newTitle0001"),
                    createBook(isbn = "isbn000002", title = "newTitle0002", newObject = false),
                    createBook(isbn = "isbn000003", title = "newTitle0003"))

        }
    }
}