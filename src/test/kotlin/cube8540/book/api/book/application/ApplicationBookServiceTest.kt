package cube8540.book.api.book.application

import cube8540.book.api.BookApiApplication
import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.BookValidatorFactory
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.Publisher
import cube8540.book.api.book.domain.Series
import cube8540.book.api.book.domain.bookAssertIgnoreFields
import cube8540.book.api.book.domain.createBook
import cube8540.book.api.book.domain.createPublisher
import cube8540.book.api.book.domain.defaultPublisherCode
import cube8540.book.api.book.repository.BookRepository
import cube8540.book.api.book.repository.PublisherRepository
import cube8540.book.api.createValidationResults
import cube8540.book.api.toDefaultInstant
import io.github.cube8540.validator.core.ValidationError
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.Clock
import java.time.LocalDate
import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.ListAssert
import org.assertj.core.internal.IgnoringFieldsComparator
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

internal class ApplicationBookServiceTest {

    private val bookRepository: BookRepository = mockk(relaxed = true)
    private val publisherRepository: PublisherRepository = mockk(relaxed = true)

    private val validatorFactory: BookValidatorFactory = mockk(relaxed = true)

    private val service = ApplicationBookService(bookRepository, publisherRepository)

    init {
        service.validatorFactory = validatorFactory
    }

    @Nested
    inner class SearchBookTest {

        @Test
        fun `search book details`() {
            val condition = createBookLookupCondition(direction = Sort.Direction.ASC)
            val pageRequest = PageRequest.of(0, 10)

            val books = listOf(createBook(isbn = "isbn0000", publisher = createPublisher()),
                createBook(isbn = "isbn0001", publisher = createPublisher()),
                createBook(isbn = "isbn0002", publisher = createPublisher()))
            val expectedCondition = createBookQueryCondition()
            val expectedPageRequest = pageRequest.withSort(Sort.by(Sort.Direction.ASC, Book::publishDate.name))

            every { bookRepository.findPageByCondition(expectedCondition, expectedPageRequest) } returns PageImpl(books, expectedPageRequest, books.size.toLong())

            val results = service.searchBooks(condition, pageRequest)
            assertThat(results.totalElements).isEqualTo(books.size.toLong())
            assertThat(results.pageable).isEqualTo(expectedPageRequest)
            bookDetailsAssertThat(results.content)
                .containsExactly(createBookDetails(isbn = "isbn0000", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0001", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0002", authors = null, indexes = null, externalLinks = null))
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
            every { validatorFactory.createValidator(createBook(isbn = "isbn000000")) } returns mockk {
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
                createBookPostRequest(isbn = "isbn000003", confirmedPublication = true)
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
                .containsExactly(createBook(isbn = "isbn000001", publisher = publisherReferenceMock),
                    createBook(isbn = "isbn000002", publisher = publisherReferenceMock),
                    createBook(isbn = "isbn000003", publisher = publisherReferenceMock, confirmedPublication = true))
            assertThat(result.successBooks).containsExactly("isbn000001", "isbn000002", "isbn000003")
        }

        @Test
        fun `update request book`() {
            val requestList: List<BookPostRequest> = listOf(
                createBookPostRequest(isbn = "isbn000001", title = "newTitle0001"),
                createBookPostRequest(isbn = "isbn000002", title = "newTitle0002"),
                createBookPostRequest(isbn = "isbn000003", title = "newTitle0003", confirmedPublication = true)
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
                .containsExactly(createBook(isbn = "isbn000001", title = "newTitle0001", newObject = false),
                    createBook(isbn = "isbn000002", title = "newTitle0002", newObject = false),
                    createBook(isbn = "isbn000003", title = "newTitle0003", newObject = false, confirmedPublication = true))
            assertThat(result.successBooks).containsExactly("isbn000001", "isbn000002", "isbn000003")
        }

        @Test
        fun `insert and update`() {
            val requestList: List<BookPostRequest> = listOf(
                createBookPostRequest(isbn = "isbn000001", title = "newTitle0001"),
                createBookPostRequest(isbn = "isbn000002", title = "newTitle0002"),
                createBookPostRequest(isbn = "isbn000003", title = "newTitle0003", confirmedPublication = true)
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
                .containsExactly(createBook(isbn = "isbn000001", title = "newTitle0001", publisher = publisherReferenceMock),
                    createBook(isbn = "isbn000002", title = "newTitle0002", newObject = false),
                    createBook(isbn = "isbn000003", title = "newTitle0003", publisher = publisherReferenceMock, confirmedPublication = true))
            assertThat(result.successBooks).containsExactly("isbn000001", "isbn000002", "isbn000003")
        }
    }

    @Nested
    inner class GetBookDetails {

        @Test
        fun `get book details if not exists in repository`() {
            val isbn = Isbn("isbn000001")

            every { bookRepository.findDetailsByIsbn(isbn) } returns null

            val result = service.getBookDetails(isbn)
            assertThat(result).isNull()
        }

        @Test
        fun `get book details if exists in repository`() {
            val isbn = Isbn("isbn000001")

            every { bookRepository.findDetailsByIsbn(isbn) } returns createBook(isbn = isbn.value, publisher = createPublisher())

            val result = service.getBookDetails(isbn)
            assertThat(result)
                .usingComparatorForFields(IgnoringFieldsComparator(*publisherDetailsAssertIgnoreFields), BookDetail::publisher.name)
                .isEqualToIgnoringGivenFields(createBookDetails(isbn = isbn.value), *bookDetailsAssertIgnoreFields)
        }
    }

    @Nested
    inner class GetSeriesList {

        @Test
        fun `get series list if given isbn and code is null`() {
            val series = Series(null, null)

            val result = service.getSeriesList(series)
            assertThat(result).isEmpty()
            verify(exactly = 0) { bookRepository.findSeries(series) }
        }

        @Test
        fun `get series list`() {
            val series = Series(isbn = Isbn("isbn00000"), code = "code000000")

            every { bookRepository.findSeries(series) } returns listOf(
                createBook(isbn = "isbn00000", publisher = createPublisher()),
                createBook(isbn = "isbn00001", publisher = createPublisher()),
                createBook(isbn = "isbn00002", publisher = createPublisher())
            )

            val result = service.getSeriesList(series)
            assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .usingElementComparatorIgnoringFields(*bookDetailsAssertIgnoreFields)
                .usingComparatorForElementFieldsWithNames(IgnoringFieldsComparator(*publisherDetailsAssertIgnoreFields), BookDetail::publisher.name)
                .containsExactly(createBookDetails(isbn = "isbn00000"), createBookDetails(isbn = "isbn00001"), createBookDetails(isbn = "isbn00002"))
        }
    }

    @Nested
    inner class LookupForNewestBook {

        @Test
        fun `request page bigger then maximum page`() {
            val randomPage = Random.nextInt(6, Int.MAX_VALUE)
            val randomSize = Random.nextInt(1, Int.MAX_VALUE)
            val pageRequest = PageRequest.of(randomPage, randomSize)

            val books = listOf(createBook(isbn = "isbn0000", publisher = createPublisher()),
                createBook(isbn = "isbn0001", publisher = createPublisher()),
                createBook(isbn = "isbn0002", publisher = createPublisher()))
            val randomTotalElements = Random.nextLong(randomSize.toLong(), Long.MAX_VALUE)
            val expectedPageRequest = PageRequest.of(5, randomSize)
                .withSort(Sort.by(Sort.Order.desc(Book::createdAt.name)))

            every { bookRepository.findAll(expectedPageRequest) } returns PageImpl(books, expectedPageRequest, randomTotalElements)

            val results = service.lookupForNewestBook(pageRequest)
            assertThat(results.totalElements).isEqualTo(randomTotalElements)
            assertThat(results.pageable).isEqualTo(expectedPageRequest)
            bookDetailsAssertThat(results.content)
                .containsExactly(createBookDetails(isbn = "isbn0000", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0001", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0002", authors = null, indexes = null, externalLinks = null))
        }

        @Test
        fun `lookup books`() {
            val randomPage = Random.nextInt(0, MAXIMUM_LOOKUP_PAGE)
            val randomSize = Random.nextInt(1, Int.MAX_VALUE)
            val pageRequest = PageRequest.of(randomPage, randomSize)

            val books = listOf(createBook(isbn = "isbn0000", publisher = createPublisher()),
                createBook(isbn = "isbn0001", publisher = createPublisher()),
                createBook(isbn = "isbn0002", publisher = createPublisher()))
            val randomTotalElements = Random.nextLong(randomSize.toLong(), Long.MAX_VALUE)
            val expectedPageRequest = pageRequest.withSort(Sort.by(Sort.Order.desc(Book::createdAt.name)))

            every { bookRepository.findAll(expectedPageRequest) } returns PageImpl(books, expectedPageRequest, randomTotalElements)

            val results = service.lookupForNewestBook(pageRequest)
            assertThat(results.totalElements).isEqualTo(randomTotalElements)
            assertThat(results.pageable).isEqualTo(expectedPageRequest)
            bookDetailsAssertThat(results.content)
                .containsExactly(createBookDetails(isbn = "isbn0000", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0001", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0002", authors = null, indexes = null, externalLinks = null))
        }
    }

    @Nested
    inner class LookupForLatestUpdate {

        @Test
        fun `request page bigger then maximum page`() {
            val randomPage = Random.nextInt(6, Int.MAX_VALUE)
            val randomSize = Random.nextInt(1, Int.MAX_VALUE)
            val pageRequest = PageRequest.of(randomPage, randomSize)

            val books = listOf(createBook(isbn = "isbn0000", publisher = createPublisher()),
                createBook(isbn = "isbn0001", publisher = createPublisher()),
                createBook(isbn = "isbn0002", publisher = createPublisher()))
            val randomTotalElements = Random.nextLong(randomSize.toLong(), Long.MAX_VALUE)
            val expectedPageRequest = PageRequest.of(5, randomSize)
                .withSort(Sort.by(Sort.Order.desc(Book::updatedAt.name)))

            every { bookRepository.findAll(expectedPageRequest) } returns PageImpl(books, expectedPageRequest, randomTotalElements)

            val results = service.lookupForLatestUpdate(pageRequest)
            assertThat(results.totalElements).isEqualTo(randomTotalElements)
            assertThat(results.pageable).isEqualTo(expectedPageRequest)
            bookDetailsAssertThat(results.content)
                .containsExactly(createBookDetails(isbn = "isbn0000", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0001", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0002", authors = null, indexes = null, externalLinks = null))
        }

        @Test
        fun `lookup books`() {
            val randomPage = Random.nextInt(0, MAXIMUM_LOOKUP_PAGE)
            val randomSize = Random.nextInt(1, Int.MAX_VALUE)
            val pageRequest = PageRequest.of(randomPage, randomSize)

            val books = listOf(createBook(isbn = "isbn0000", publisher = createPublisher()),
                createBook(isbn = "isbn0001", publisher = createPublisher()),
                createBook(isbn = "isbn0002", publisher = createPublisher()))
            val randomTotalElements = Random.nextLong(randomSize.toLong(), Long.MAX_VALUE)
            val expectedPageRequest = pageRequest.withSort(Sort.by(Sort.Order.desc(Book::updatedAt.name)))

            every { bookRepository.findAll(expectedPageRequest) } returns PageImpl(books, expectedPageRequest, randomTotalElements)

            val results = service.lookupForLatestUpdate(pageRequest)
            assertThat(results.totalElements).isEqualTo(randomTotalElements)
            assertThat(results.pageable).isEqualTo(expectedPageRequest)
            bookDetailsAssertThat(results.content)
                .containsExactly(createBookDetails(isbn = "isbn0000", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0001", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0002", authors = null, indexes = null, externalLinks = null))
        }
    }

    @Nested
    inner class LookupForPublishedToday {

        @Test
        fun `request page bigger then maximum page`() {
            val randomPage = Random.nextInt(6, Int.MAX_VALUE)
            val randomSize = Random.nextInt(1, Int.MAX_VALUE)
            val randomLocalDate = randomLocalDate()
            val pageRequest = PageRequest.of(randomPage, randomSize)

            val books = listOf(createBook(isbn = "isbn0000", publisher = createPublisher()),
                createBook(isbn = "isbn0001", publisher = createPublisher()),
                createBook(isbn = "isbn0002", publisher = createPublisher()))
            val randomTotalElements = Random.nextLong(randomSize.toLong(), Long.MAX_VALUE)
            val expectedPageRequest = PageRequest.of(5, randomSize)
                .withSort(Sort.by(Sort.Order.desc(Book::createdAt.name)))

            ApplicationBookService.clock = Clock.fixed(randomLocalDate.toDefaultInstant(), BookApiApplication.DEFAULT_TIME_ZONE.toZoneId())
            every { bookRepository.findByPublishDate(randomLocalDate, expectedPageRequest) } returns PageImpl(books, expectedPageRequest, randomTotalElements)

            val results = service.lookupForPublishedToday(pageRequest)
            assertThat(results.totalElements).isEqualTo(randomTotalElements)
            assertThat(results.pageable).isEqualTo(expectedPageRequest)
            bookDetailsAssertThat(results.content)
                .containsExactly(createBookDetails(isbn = "isbn0000", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0001", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0002", authors = null, indexes = null, externalLinks = null))
        }

        @Test
        fun `lookup books`() {
            val randomPage = Random.nextInt(0, MAXIMUM_LOOKUP_PAGE)
            val randomSize = Random.nextInt(1, Int.MAX_VALUE)
            val randomLocalDate = randomLocalDate()
            val pageRequest = PageRequest.of(randomPage, randomSize)

            val books = listOf(createBook(isbn = "isbn0000", publisher = createPublisher()),
                createBook(isbn = "isbn0001", publisher = createPublisher()),
                createBook(isbn = "isbn0002", publisher = createPublisher()))
            val randomTotalElements = Random.nextLong(randomSize.toLong(), Long.MAX_VALUE)
            val expectedPageRequest = pageRequest.withSort(Sort.by(Sort.Order.desc(Book::createdAt.name)))

            ApplicationBookService.clock = Clock.fixed(randomLocalDate.toDefaultInstant(), BookApiApplication.DEFAULT_TIME_ZONE.toZoneId())
            every { bookRepository.findByPublishDate(randomLocalDate, expectedPageRequest) } returns PageImpl(books, expectedPageRequest, randomTotalElements)

            val results = service.lookupForPublishedToday(pageRequest)
            assertThat(results.totalElements).isEqualTo(randomTotalElements)
            assertThat(results.pageable).isEqualTo(expectedPageRequest)
            bookDetailsAssertThat(results.content)
                .containsExactly(createBookDetails(isbn = "isbn0000", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0001", authors = null, indexes = null, externalLinks = null),
                    createBookDetails(isbn = "isbn0002", authors = null, indexes = null, externalLinks = null))
        }
    }

    private fun bookDetailsAssertThat(books: List<BookDetail>): ListAssert<BookDetail> = assertThat(books)
        .usingRecursiveFieldByFieldElementComparator()
        .usingElementComparatorIgnoringFields(*bookDetailsAssertIgnoreFields)
        .usingComparatorForElementFieldsWithNames(IgnoringFieldsComparator(*publisherDetailsAssertIgnoreFields), BookDetail::publisher.name)

    private fun randomLocalDate(): LocalDate =
        LocalDate.ofEpochDay(Random.nextLong(LocalDate.of(1970, 1, 1).toEpochDay(), LocalDate.MAX.toEpochDay()))
}