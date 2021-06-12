package cube8540.book.api.book.infra

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.Series
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultBookSeriesValidationRuleTest {

    private val validationRule = DefaultBookSeriesValidationRule()

    @Test
    fun `validation when series is null`() {
        val book: Book = mockk(relaxed = true) {
            every { series } returns null
        }

        val result = validationRule.isValid(book)
        assertThat(result).isTrue
    }

    @Test
    fun `validation when series is not valid`() {
        val series: Series = mockk(relaxed = true) {
            every { isValid() } returns false
        }
        val book: Book = mockk(relaxed = true)

        every { book.series } returns series

        val result = validationRule.isValid(book)
        assertThat(result).isFalse
    }

    @Test
    fun `validation when series is valid`() {
        val series: Series = mockk(relaxed = true) {
            every { isValid() } returns true
        }
        val book: Book = mockk(relaxed = true)

        every { book.series } returns series

        val result = validationRule.isValid(book)
        assertThat(result).isTrue
    }

}