package cube8540.book.api.book.infra

import cube8540.book.api.book.domain.BookInvalidException
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.defaultPublisherCode
import cube8540.book.api.book.domain.defaultTitle
import cube8540.book.api.elasticsearch.book.application.BookElasticsearchCondition
import cube8540.book.api.elasticsearch.book.application.BookElasticsearchService
import cube8540.book.api.elasticsearch.book.application.DocumentPageInvalidException
import cube8540.book.api.elasticsearch.book.application.createBookSearchCondition
import cube8540.book.api.elasticsearch.book.domain.createBookDocument
import io.github.cube8540.validator.core.ValidationError
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

internal class BookElasticsearchExchangerTest {

    private val elasticsearchService: BookElasticsearchService = mockk(relaxed = true)

    private val exchanger = BookElasticsearchExchanger(elasticsearchService)

    @Test
    fun `elasticsearch throws page invalid exception`() {
        val randomPage = Random.nextInt(0, 100)
        val randomSize = Random.nextInt(0, 100)

        val searchRequest = createExternalSearchRequest()
        val pageable = PageRequest.of(randomPage, randomSize)

        val elasticsearchRequest = createBookSearchCondition(
            title = defaultTitle, from = defaultPublishFrom, to = defaultPublishTo, publisherCode = defaultPublisherCode)
        every { elasticsearchService.search(elasticsearchRequest, pageable) } throws DocumentPageInvalidException("message")

        val thrown = catchThrowable { exchanger.search(searchRequest, pageable) }
        assertThat(thrown).isInstanceOf(BookInvalidException::class.java)
        assertThat((thrown as BookInvalidException).errors)
            .containsExactly(ValidationError(exchanger.pagePropertyName, "message"))
    }

    @Test
    fun `convert search condition to elasticsearch request`() {
        val randomPage = Random.nextInt(0, 100)
        val randomSize = Random.nextInt(0, 100)
        val randomTotalSize = Random.nextLong((randomPage * randomSize).toLong(), Long.MAX_VALUE)

        val searchRequest = createExternalSearchRequest()
        val pageable = PageRequest.of(randomPage, randomSize)

        val captor = slot<BookElasticsearchCondition>()
        val documents = listOf(createBookDocument(isbn = "isbn00001"), createBookDocument(isbn = "isbn00002"), createBookDocument(isbn = "isbn00003"))
        every { elasticsearchService.search(capture(captor), pageable) } returns PageImpl(documents, pageable, randomTotalSize)

        exchanger.search(searchRequest, pageable)
        val expectedBookElasticsearchCondition = createBookSearchCondition(title = defaultTitle, from = defaultPublishFrom, to = defaultPublishTo, publisherCode = defaultPublisherCode)
        assertThat(captor.captured).isEqualToComparingFieldByField(expectedBookElasticsearchCondition)
    }

    @Test
    fun `returns elasticsearch results isbn`() {
        val randomPage = Random.nextInt(0, 100)
        val randomSize = Random.nextInt(0, 100)
        val randomTotalSize = Random.nextLong((randomPage * randomSize).toLong(), Long.MAX_VALUE)

        val searchRequest = createExternalSearchRequest()
        val pageable = PageRequest.of(randomPage, randomSize)

        val elasticsearchRequest = createBookSearchCondition(
            title = defaultTitle, from = defaultPublishFrom, to = defaultPublishTo, publisherCode = defaultPublisherCode)
        val documents = listOf(createBookDocument(isbn = "isbn00001"), createBookDocument(isbn = "isbn00002"), createBookDocument(isbn = "isbn00003"))
        every { elasticsearchService.search(elasticsearchRequest, pageable) } returns PageImpl(documents, pageable, randomTotalSize)

        val results = exchanger.search(searchRequest, pageable)
        assertThat(results).containsExactly(Isbn("isbn00001"), Isbn("isbn00002"), Isbn("isbn00003"))
        assertThat(results.totalElements).isEqualTo(randomTotalSize)
        assertThat(results.pageable).isEqualTo(pageable)
    }
}