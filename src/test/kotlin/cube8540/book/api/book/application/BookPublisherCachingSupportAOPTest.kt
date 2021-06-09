package cube8540.book.api.book.application

import cube8540.book.api.book.repository.PublisherRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
        assertThat(publisherCaptor.captured).isEqualTo(
            listOf("publisher0000", "publisher0001", "publisher0002")
        )
    }

    @Test
    fun `caching publisher before book upsert when request publisher code count is grater then 500`() {
        val upsertRequests = (0..1300).map { createBookPostRequest(isbn = "isbn-$it", publisherCode = "publisher-$it") }
        val publisherCaptor = ArrayList<List<String>>()

        every { publisherRepository.findAllById(capture(publisherCaptor)) } returns emptyList()

        aop.cachingPublisherBeforeBookUpsert(upsertRequests)
        assertThat(publisherCaptor).hasSize(3).isEqualTo(listOf(
            (0..499).map { "publisher-$it" }.toList(),
            (500..999).map { "publisher-$it" }.toList(),
            (1000..1300).map { "publisher-$it" }.toList()
        ))
    }
}