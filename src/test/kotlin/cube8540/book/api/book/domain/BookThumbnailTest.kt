package cube8540.book.api.book.domain

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI

internal class BookThumbnailTest {

    @Nested
    inner class MergedTest {
        private val thumbnailBase = BookThumbnail()

        @Test
        fun `change large thumbnail when given uri is null`() {
            val largeThumbnail = null
            val thumbnail = BookThumbnail(largeThumbnail = largeThumbnail)

            val originalThumbnail: URI = mockk(relaxed = true)
            thumbnailBase.largeThumbnail = originalThumbnail

            thumbnailBase.mergeThumbnail(thumbnail)
            assertThat(thumbnailBase.largeThumbnail).isEqualTo(originalThumbnail)
        }

        @Test
        fun `change large thumbnail`() {
            val largeThumbnail: URI = mockk(relaxed = true)
            val thumbnail = BookThumbnail(largeThumbnail = largeThumbnail)

            val originalThumbnail: URI = mockk(relaxed = true)
            thumbnailBase.largeThumbnail = originalThumbnail

            thumbnailBase.mergeThumbnail(thumbnail)
            assertThat(thumbnailBase.largeThumbnail).isEqualTo(largeThumbnail)
        }

        @Test
        fun `change medium thumbnail when given uri is null`() {
            val mediumThumbnail = null
            val thumbnail = BookThumbnail(mediumThumbnail = mediumThumbnail)

            val originalThumbnail: URI = mockk(relaxed = true)
            thumbnailBase.mediumThumbnail = originalThumbnail

            thumbnailBase.mergeThumbnail(thumbnail)
            assertThat(thumbnailBase.mediumThumbnail).isEqualTo(originalThumbnail)
        }

        @Test
        fun `change medium thumbnail`() {
            val mediumThumbnail: URI = mockk(relaxed = true)
            val thumbnail = BookThumbnail(mediumThumbnail = mediumThumbnail)

            val originalThumbnail: URI = mockk(relaxed = true)
            thumbnailBase.mediumThumbnail = originalThumbnail

            thumbnailBase.mergeThumbnail(thumbnail)
            assertThat(thumbnailBase.mediumThumbnail).isEqualTo(mediumThumbnail)
        }

        @Test
        fun `change small thumbnail when given uri is null`() {
            val smallThumbnail = null
            val thumbnail = BookThumbnail(smallThumbnail = smallThumbnail)

            val originalThumbnail: URI = mockk(relaxed = true)
            thumbnailBase.smallThumbnail = originalThumbnail

            thumbnailBase.mergeThumbnail(thumbnail)
            assertThat(thumbnailBase.smallThumbnail).isEqualTo(originalThumbnail)
        }

        @Test
        fun `change small thumbnail`() {
            val smallThumbnail: URI = mockk(relaxed = true)
            val thumbnail = BookThumbnail(smallThumbnail = smallThumbnail)

            val originalThumbnail: URI = mockk(relaxed = true)
            thumbnailBase.smallThumbnail = originalThumbnail

            thumbnailBase.mergeThumbnail(thumbnail)
            assertThat(thumbnailBase.smallThumbnail).isEqualTo(smallThumbnail)
        }
    }

}