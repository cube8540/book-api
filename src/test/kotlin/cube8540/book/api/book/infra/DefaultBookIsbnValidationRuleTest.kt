package cube8540.book.api.book.infra

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.Isbn
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultBookIsbnValidationRuleTest {

    private val validationRule = DefaultBookIsbnValidationRule()

    @Test
    fun `validation when isbn is not valid`() {
        val isbn: Isbn = mockk(relaxed = true) {
            every { isValid() } returns false
        }
        val book: Book = mockk(relaxed = true)

        every { book.isbn } returns isbn

        val result = validationRule.isValid(book)
        assertThat(result).isFalse
    }

    @Test
    fun `validation when isbn is valid`() {
        val isbn: Isbn = mockk(relaxed = true) {
            every { isValid() } returns true
        }
        val book: Book = mockk(relaxed = true)

        every { book.isbn } returns isbn

        val result = validationRule.isValid(book)
        assertThat(result).isTrue
    }

}