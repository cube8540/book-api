package cube8540.book.api.book.application

import cube8540.book.api.book.domain.createPublisher
import cube8540.book.api.book.repository.PublisherContainer
import cube8540.book.api.book.repository.PublisherContainerHolder
import cube8540.book.api.book.repository.PublisherRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.aspectj.lang.ProceedingJoinPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class BookPublisherCachingSupportAOPTest {

    private val publisherRepository: PublisherRepository = mockk(relaxed = true)

    private val aop =  BookPublisherCachingSupportAOP(publisherRepository)

    @Test
    fun `caching requested publishers`() {
        val requests = listOf(
            createBookPostRequest(isbn = "isbn00000", publisherCode = "code00000"),
            createBookPostRequest(isbn = "isbn00001", publisherCode = "code00001"),
            createBookPostRequest(isbn = "isbn00002", publisherCode = "code00002")
        )
        val storedPublishers = listOf(createPublisher(code = "code00000"), createPublisher(code = "code00001"), createPublisher(code = "code00002"))
        val requestPublisherCodes = requests.map { it.publisherCode }
        val proceedingJoinPoint: ProceedingJoinPoint = mockk(relaxed = true)

        val container: PublisherContainer = mockk(relaxed = true)

        PublisherContainerHolder.setContainer(container)
        every { publisherRepository.findAllById(requestPublisherCodes) } returns storedPublishers
        every { proceedingJoinPoint.proceed() } returns Any()

        aop.cachingPublisherBeforeBookUpsert(proceedingJoinPoint, requests)
        verify {
            container.storeAll(storedPublishers)
            proceedingJoinPoint.proceed()
        }
        assertThat(PublisherContainerHolder.getContainer().isEmpty()).isTrue
    }

    @AfterEach
    fun afterEach() {
        PublisherContainerHolder.clearContainer()
    }
}