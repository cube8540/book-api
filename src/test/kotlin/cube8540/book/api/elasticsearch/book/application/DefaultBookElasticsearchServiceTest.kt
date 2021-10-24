package cube8540.book.api.elasticsearch.book.application

import cube8540.book.api.elasticsearch.book.document.BookDocument
import cube8540.book.api.elasticsearch.book.domain.createBookDocument
import cube8540.book.api.elasticsearch.book.domain.defaultPublishFrom
import cube8540.book.api.elasticsearch.book.domain.defaultPublishTo
import cube8540.book.api.elasticsearch.book.domain.defaultPublisherCode
import cube8540.book.api.elasticsearch.book.domain.defaultTitle
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.MatchQueryBuilder
import org.elasticsearch.index.query.RangeQueryBuilder
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.core.query.Query

internal class DefaultBookElasticsearchServiceTest {

    private val elasticsearchOperations: ElasticsearchOperations = mockk(relaxed = true)

    private val service = DefaultBookElasticsearchService(elasticsearchOperations)

    @Test
    fun `request page is bigger then max document size`() {
        val randomSize: Int = Random.nextInt(10, 20)
        val maximumPage = ((service.maxDocumentSize - randomSize) / randomSize) + 1

        val condition: BookElasticsearchCondition = createBookSearchCondition()
        val pageable: Pageable = PageRequest.of(maximumPage + 1, randomSize)

        val thrown = catchThrowable { service.search(condition, pageable) }
        assertThat(thrown).isInstanceOf(DocumentPageInvalidException::class.java)
    }

    @Test
    fun `publish from condition is null`() {
        val randomSize: Int = Random.nextInt(10, 20)
        val maximumPage = ((service.maxDocumentSize - randomSize) / randomSize) + 1
        val randomPage: Int = Random.nextInt(0, maximumPage)

        val condition: BookElasticsearchCondition = createBookSearchCondition(from = null)
        val pageable = PageRequest.of(randomPage, randomSize)

        val queryCaptor = slot<NativeSearchQuery>()

        every { elasticsearchOperations.search(capture(queryCaptor), BookDocument::class.java) } returns mockk(relaxed = true)

        service.search(condition, pageable)
        assertThat(queryCaptor.captured.pageable).isEqualTo(pageable)
        assertThat(queryCaptor.captured)
            .extracting { it.query }.isInstanceOf(BoolQueryBuilder::class.java)
        assertThat((queryCaptor.captured.query as BoolQueryBuilder).must())
            .containsExactly(MatchQueryBuilder(BookDocument.titleFieldName, defaultTitle))
        assertThat((queryCaptor.captured.query as BoolQueryBuilder).filter())
            .containsExactly(MatchQueryBuilder(BookDocument.publisherCodeFieldName, defaultPublisherCode))
    }

    @Test
    fun `publish to condition is null`() {
        val randomSize: Int = Random.nextInt(10, 20)
        val maximumPage = ((service.maxDocumentSize - randomSize) / randomSize) + 1
        val randomPage: Int = Random.nextInt(0, maximumPage)

        val condition: BookElasticsearchCondition = createBookSearchCondition(to = null)
        val pageable = PageRequest.of(randomPage, randomSize)

        val queryCaptor = slot<NativeSearchQuery>()

        every { elasticsearchOperations.search(capture(queryCaptor), BookDocument::class.java) } returns mockk(relaxed = true)

        service.search(condition, pageable)
        assertThat(queryCaptor.captured.pageable).isEqualTo(pageable)
        assertThat(queryCaptor.captured)
            .extracting { it.query }.isInstanceOf(BoolQueryBuilder::class.java)
        assertThat((queryCaptor.captured.query as BoolQueryBuilder).must())
            .containsExactly(MatchQueryBuilder(BookDocument.titleFieldName, defaultTitle))
        assertThat((queryCaptor.captured.query as BoolQueryBuilder).filter())
            .containsExactly(MatchQueryBuilder(BookDocument.publisherCodeFieldName, defaultPublisherCode))
    }

    @Test
    fun `publisher code condition is null`() {
        val randomSize: Int = Random.nextInt(10, 20)
        val maximumPage = ((service.maxDocumentSize - randomSize) / randomSize) + 1
        val randomPage: Int = Random.nextInt(0, maximumPage)

        val condition: BookElasticsearchCondition = createBookSearchCondition(publisherCode = null)
        val pageable = PageRequest.of(randomPage, randomSize)

        val queryCaptor = slot<NativeSearchQuery>()

        every { elasticsearchOperations.search(capture(queryCaptor), BookDocument::class.java) } returns mockk(relaxed = true)

        service.search(condition, pageable)
        assertThat(queryCaptor.captured.pageable).isEqualTo(pageable)
        assertThat(queryCaptor.captured)
            .extracting { it.query }.isInstanceOf(BoolQueryBuilder::class.java)
        assertThat((queryCaptor.captured.query as BoolQueryBuilder).must())
            .containsExactly(MatchQueryBuilder(BookDocument.titleFieldName, defaultTitle))
        assertThat((queryCaptor.captured.query as BoolQueryBuilder).filter())
            .containsExactly(RangeQueryBuilder(BookDocument.publishDateFieldName).from(defaultPublishFrom).to(defaultPublishTo))
    }

    @Test
    fun `search book returns elasticsearch operations search results`() {
        val randomSize: Int = Random.nextInt(10, 20)
        val maximumPage = ((service.maxDocumentSize - randomSize) / randomSize) + 1
        val randomPage: Int = Random.nextInt(0, maximumPage)

        val randomTotalHits = randomTotalElements(randomSize.toLong(), randomPage.toLong(), maximumPage.toLong())
        val searchResults: SearchHits<BookDocument> = createSearchHits(totalHits = randomTotalHits, hits = listOf(
            createSearchHit(createBookDocument(isbn = "isbn000001")),
            createSearchHit(createBookDocument(isbn = "isbn000002")),
            createSearchHit(createBookDocument(isbn = "isbn000003"))
        ))

        every { elasticsearchOperations.search(any<Query>(), BookDocument::class.java) } returns searchResults

        val result = service.search(createBookSearchCondition(), PageRequest.of(randomPage, randomSize))
        assertThat(result.totalElements).isEqualTo(randomTotalHits)
        assertThat(result.content).containsExactly(
            createBookDocument(isbn = "isbn000001"),
            createBookDocument(isbn = "isbn000002"),
            createBookDocument(isbn = "isbn000003"))
    }

    private fun randomTotalElements(size: Long, page: Long, maxPage: Long): Long = Random.nextLong(page * size + size, maxPage * size + size)
}