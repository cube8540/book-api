package cube8540.book.api.book.domain

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SeriesAttributeTest {

    @Test
    fun `validation when isbn is null`() {
        val seriesAttribute = Series(null, null)

        assertThat(seriesAttribute.isValid()).isTrue
    }

    @Test
    fun `validation when isbn is not invalid`() {
        val isbn: Isbn = mockk(relaxed = true) {
            every { isValid() } returns false
        }

        val seriesAttribute = Series(isbn, null)
        assertThat(seriesAttribute.isValid()).isFalse
    }

    @Test
    fun `validation when isbn is valid`() {
        val isbn: Isbn = mockk(relaxed = true) {
            every { isValid() } returns true
        }

        val seriesAttribute = Series(isbn, null)
        assertThat(seriesAttribute.isValid()).isTrue
    }
}