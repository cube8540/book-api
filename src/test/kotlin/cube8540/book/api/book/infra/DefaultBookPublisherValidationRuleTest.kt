package cube8540.book.api.book.infra

import cube8540.book.api.book.application.PublisherDetailsService
import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.Publisher
import cube8540.book.api.book.domain.defaultPublisherCode
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultBookPublisherValidationRuleTest {

    private val publisherDetailsService: PublisherDetailsService = mockk(relaxed = true)

    private val validationRule = DefaultBookPublisherValidationRule()

    init {
        validationRule.publisherDetailsService = publisherDetailsService
    }

    @Test
    fun `validation when book publisher not found`() {
        val book: Book = mockk(relaxed = true)

        every { book.publisher } returns Publisher.fake(defaultPublisherCode)
        every { publisherDetailsService.existsPublisher(defaultPublisherCode) } returns false

        val result = validationRule.isValid(book)
        assertThat(result).isFalse
    }

    @Test
    fun `validation when book publisher is exists`() {
        val book: Book = mockk(relaxed = true)

        every { book.publisher } returns Publisher.fake(defaultPublisherCode)
        every { publisherDetailsService.existsPublisher(defaultPublisherCode) } returns true

        val result = validationRule.isValid(book)
        assertThat(result).isTrue
    }
}