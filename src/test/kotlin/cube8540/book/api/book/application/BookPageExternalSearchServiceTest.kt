package cube8540.book.api.book.application

import cube8540.book.api.book.domain.BookInvalidException
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.createBook
import cube8540.book.api.book.domain.createPublisher
import cube8540.book.api.book.domain.defaultPublisherCode
import cube8540.book.api.book.domain.defaultTitle
import cube8540.book.api.book.infra.BookExternalSearchExchanger
import cube8540.book.api.book.infra.BookExternalSearchRequest
import cube8540.book.api.book.infra.createExternalSearchRequest
import cube8540.book.api.book.repository.BookRepository
import io.github.cube8540.validator.core.ValidationError
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.internal.IgnoringFieldsComparator
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

internal class BookPageExternalSearchServiceTest {

    private val externalSearchExchanger: BookExternalSearchExchanger = mockk(relaxed = true)
    private val bookRepository: BookRepository = mockk(relaxed = true)

    private val service = BookPageExternalSearchService(externalSearchExchanger, bookRepository)

    @Test
    fun `search condition title is null`() {
        val randomPage = Random.nextInt(0, 100)
        val randomSize = Random.nextInt(1, 100)

        val condition = createBookLookupCondition(title = null)
        val pageable = PageRequest.of(randomPage, randomSize)

        val thrown = catchThrowable { service.searchBooks(condition, pageable) }
        assertThat(thrown).isInstanceOf(BookInvalidException::class.java)
        assertThat((thrown as BookInvalidException).errors)
            .containsExactly(ValidationError(service.titlePropertyName, service.titleNullErrorMessage))
    }

    @Test
    fun `convert search condition to external search request`() {
        val randomPage = Random.nextInt(0, 100)
        val randomSize = Random.nextInt(1, 100)
        val randomTotalSize = Random.nextLong((randomPage * randomSize).toLong(), Long.MAX_VALUE)

        val condition = createBookLookupCondition()
        val pageable = PageRequest.of(randomPage, randomSize)

        val captor = slot<BookExternalSearchRequest>()

        val isbnList = listOf(Isbn("isbn00001"), Isbn("isbn00002"), Isbn("isbn00003"))
        every { externalSearchExchanger.search(capture(captor), pageable) } returns PageImpl(isbnList, pageable, randomTotalSize)

        service.searchBooks(condition, pageable)
        val expectedExternalSearchRequest = createExternalSearchRequest(
            title = defaultTitle, from = defaultPublishFrom, to = defaultPublishTo, publisherCode = defaultPublisherCode)
        assertThat(captor.captured).isEqualToComparingFieldByField(expectedExternalSearchRequest)
    }

    @Test
    fun `returns book details after convert search results to book details`() {
        val randomPage = Random.nextInt(0, 100)
        val randomSize = Random.nextInt(1, 100)
        val randomTotalSize = Random.nextLong((randomPage * randomSize).toLong(), Long.MAX_VALUE)

        val condition = createBookLookupCondition()
        val pageable = PageRequest.of(randomPage, randomSize)

        val isbnList = listOf(Isbn("isbn00001"), Isbn("isbn00002"), Isbn("isbn00003"))
        val books = listOf(createBook(isbn = "isbn00001", publisher = createPublisher()),
            createBook(isbn = "isbn00002", publisher = createPublisher()),
            createBook(isbn = "isbn00003", publisher = createPublisher()))
        val externalSearchRequest = createExternalSearchRequest(
            title = defaultTitle, from = defaultPublishFrom, to = defaultPublishTo, publisherCode = defaultPublisherCode)
        every { externalSearchExchanger.search(externalSearchRequest, pageable) } returns PageImpl(isbnList, pageable, randomTotalSize)
        every { bookRepository.findDetailsByIsbn(isbnList) } returns books

        val results = service.searchBooks(condition, pageable)
        assertThat(results.pageable).isEqualTo(pageable)
        assertThat(results.totalElements).isEqualTo(randomTotalSize)
        assertThat(results.content)
            .usingRecursiveFieldByFieldElementComparator()
            .usingElementComparatorIgnoringFields(*bookDetailsAssertIgnoreFields)
            .usingComparatorForElementFieldsWithNames(IgnoringFieldsComparator(*publisherDetailsAssertIgnoreFields), BookDetail::publisher.name)
            .containsExactly(createBookDetails(isbn = "isbn00001"), createBookDetails("isbn00002"), createBookDetails("isbn00003"))
    }

    @Test
    fun `sorting book details in the order of the results`() {
        val randomPage = Random.nextInt(0, 100)
        val randomSize = Random.nextInt(1, 100)
        val randomTotalSize = Random.nextLong((randomPage * randomSize).toLong(), Long.MAX_VALUE)

        val condition = createBookLookupCondition()
        val pageable = PageRequest.of(randomPage, randomSize)

        val isbnList = listOf(Isbn("isbn00001"), Isbn("isbn00002"), Isbn("isbn00003"))
        val books = listOf(createBook(isbn = "isbn00002", publisher = createPublisher()),
            createBook(isbn = "isbn00003", publisher = createPublisher()),
            createBook(isbn = "isbn00001", publisher = createPublisher()))
        val externalSearchRequest = createExternalSearchRequest(
            title = defaultTitle, from = defaultPublishFrom, to = defaultPublishTo, publisherCode = defaultPublisherCode)
        every { externalSearchExchanger.search(externalSearchRequest, pageable) } returns PageImpl(isbnList, pageable, randomTotalSize)
        every { bookRepository.findDetailsByIsbn(isbnList) } returns books

        val results = service.searchBooks(condition, pageable)
        assertThat(results.content)
            .usingRecursiveFieldByFieldElementComparator()
            .usingElementComparatorIgnoringFields(*bookDetailsAssertIgnoreFields)
            .usingComparatorForElementFieldsWithNames(IgnoringFieldsComparator(*publisherDetailsAssertIgnoreFields), BookDetail::publisher.name)
            .containsExactly(createBookDetails(isbn = "isbn00001"), createBookDetails("isbn00002"), createBookDetails("isbn00003"))
    }
}