package cube8540.book.api.book.application

import cube8540.book.api.book.domain.PublisherNotFoundException
import cube8540.book.api.book.domain.createPublisher
import cube8540.book.api.book.domain.defaultPublisherCode
import cube8540.book.api.book.repository.PublisherRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

internal class DefaultPublisherDetailsServiceTest {

    private val publisherRepository: PublisherRepository = mockk(relaxed = true)

    private val service = DefaultPublisherDetailsService(publisherRepository)

    @Nested
    inner class ExistsTest {

        @Test
        fun `not exists publisher`() {
            every { publisherRepository.findById(defaultPublisherCode) } returns Optional.empty()

            val result = service.existsPublisher(defaultPublisherCode)
            assertThat(result).isFalse
        }

        @Test
        fun `exists publisher`() {
            val publisher = createPublisher(code = defaultPublisherCode)

            every { publisherRepository.findById(defaultPublisherCode) } returns Optional.of(publisher)

            val result = service.existsPublisher(defaultPublisherCode)
            assertThat(result).isTrue
        }
    }

    @Nested
    inner class LoadDetailsTest {

        @Test
        fun `load publisher details when publisher is not exists`() {
            every { publisherRepository.findDetailsByCode(defaultPublisherCode) } returns null

            val thrown = catchThrowable { service.loadByPublisherCode(defaultPublisherCode) }
            assertThat(thrown).isInstanceOf(PublisherNotFoundException::class.java)
        }

        @Test
        fun `load publisher details`() {
            val publisher = createPublisher(code = defaultPublisherCode)

            every { publisherRepository.findDetailsByCode(defaultPublisherCode) } returns publisher

            val result = service.loadByPublisherCode(defaultPublisherCode)
            assertThat(result)
                .isEqualToIgnoringGivenFields(createPublisherDetails(code = defaultPublisherCode), *publisherDetailsAssertIgnoreFields)
        }
    }
}