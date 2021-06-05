package cube8540.book.api.book.domain

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SeriesAttributeTest {

    @Nested
    inner class ValidationTest {
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

    @Nested
    inner class MergedTest {
        private val seriesBase = Series()

        @Test
        fun `change series isbn when given isbn is null`() {
            val seriesIsbn = null
            val series = Series(isbn = seriesIsbn)

            val originalSeriesIsbn = Isbn("originalSeriesIsbn000001")
            seriesBase.isbn = originalSeriesIsbn

            seriesBase.mergeSeries(series)
            assertThat(seriesBase.isbn).isEqualTo(originalSeriesIsbn)
        }

        @Test
        fun `change series isbn when given isbn is empty`() {
            val seriesIsbn = Isbn("")
            val series = Series(isbn = seriesIsbn)

            val originalSeriesIsbn = Isbn("originalSeriesIsbn000001")
            seriesBase.isbn = originalSeriesIsbn

            seriesBase.mergeSeries(series)
            assertThat(seriesBase.isbn).isEqualTo(originalSeriesIsbn)
        }

        @Test
        fun `change series isbn`() {
            val seriesIsbn = Isbn("mergedIsbn000001")
            val series = Series(isbn = seriesIsbn)

            val originalSeriesIsbn = Isbn("originalSeriesIsbn000001")
            seriesBase.isbn = originalSeriesIsbn

            seriesBase.mergeSeries(series)
            assertThat(seriesBase.isbn).isEqualTo(seriesIsbn)
        }

        @Test
        fun `change series code when given series code is null`() {
            val seriesCode = null
            val series = Series(code = seriesCode)

            val originalSeriesCode = "originalSeriesCode00001"
            seriesBase.code = originalSeriesCode

            seriesBase.mergeSeries(series)
            assertThat(seriesBase.code).isEqualTo(originalSeriesCode)
        }

        @Test
        fun `change series code when given series code is empty`() {
            val seriesCode = ""
            val series = Series(code = seriesCode)

            val originalSeriesCode = "originalSeriesCode00001"
            seriesBase.code = originalSeriesCode

            seriesBase.mergeSeries(series)
            assertThat(seriesBase.code).isEqualTo(originalSeriesCode)
        }

        @Test
        fun `change series code`() {
            val seriesCode = "mergedSeriesCode000001"
            val series = Series(code = seriesCode)

            val originalSeriesCode = "originalSeriesCode00001"
            seriesBase.code = originalSeriesCode

            seriesBase.mergeSeries(series)
            assertThat(seriesBase.code).isEqualTo(seriesCode)
        }
    }
}