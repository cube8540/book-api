package cube8540.book.api.book.infra

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.Publisher
import cube8540.book.api.book.domain.defaultPublisherCode
import cube8540.book.api.book.repository.PublisherContainer
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultBookPublisherValidationRuleTest {

    private val validationRule = DefaultBookPublisherValidationRule()

    private val publisherContainer: PublisherContainer = mockk(relaxed = true)

    init {
        validationRule.container = publisherContainer
    }

    @Test
    fun `validation when book publisher not found`() {
        val book: Book = mockk(relaxed = true)

        every { book.publisher } returns Publisher.fake(defaultPublisherCode)
        every { publisherContainer.getPublisher(defaultPublisherCode) } returns null

        val result = validationRule.isValid(book)
        assertThat(result).isFalse
    }

    @Test
    fun `validation when book publisher is exists`() {
        val book: Book = mockk(relaxed = true)
        val publisher: Publisher = mockk(relaxed = true)

        every { book.publisher } returns Publisher.fake(defaultPublisherCode)
        every { publisherContainer.getPublisher(defaultPublisherCode) } returns publisher

        val result = validationRule.isValid(book)
        assertThat(result).isTrue
    }
}