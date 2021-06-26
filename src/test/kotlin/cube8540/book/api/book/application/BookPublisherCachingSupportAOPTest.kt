package cube8540.book.api.book.application

import cube8540.book.api.book.repository.PublisherRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class BookPublisherCachingSupportAOPTest {

    private val publisherRepository: PublisherRepository = mockk(relaxed = true)
    private val aop = BookPublisherCachingSupportAOP()

    init {
        aop.publisherRepository = publisherRepository
    }

    @Test
    fun `caching publisher before book upsert`() {
        val upsertRequests = listOf(
            createBookPostRequest(isbn = "isbn0000", publisherCode = "publisher0000"),
            createBookPostRequest(isbn = "isbn0001", publisherCode = "publisher0001"),
            createBookPostRequest(isbn = "isbn0002", publisherCode = "publisher0002")
        )
        val publisherCaptor = slot<List<String>>()

        every { publisherRepository.findAllById(capture(publisherCaptor)) } returns emptyList()

        aop.cachingPublisherBeforeBookUpsert(upsertRequests)
        assertThat(publisherCaptor.captured)
            .containsExactly("publisher0000", "publisher0001", "publisher0002")
    }

    @Test
    fun `caching publisher before book upsert when request publisher code count is grater then chunk`() {
        val randomChunk = Random.nextInt(100, 500)
        val upsertRequests = (0 until (randomChunk * 3)).map { createBookPostRequest(isbn = "isbn-$it", publisherCode = "publisher-$it") }
        val publisherCaptor = ArrayList<List<String>>()

        aop.chunkSize = randomChunk
        every { publisherRepository.findAllById(capture(publisherCaptor)) } returns emptyList()

        aop.cachingPublisherBeforeBookUpsert(upsertRequests)
        assertThat(publisherCaptor).hasSize(3)
            .containsExactly(
                (0 until randomChunk).map { "publisher-$it" }.toList(),
                (randomChunk until (randomChunk * 2)).map { "publisher-$it" }.toList(),
                ((randomChunk * 2) until (randomChunk * 3)).map { "publisher-$it" }.toList()
            )
    }
}