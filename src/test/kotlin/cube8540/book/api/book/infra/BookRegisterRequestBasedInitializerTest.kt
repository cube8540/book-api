package cube8540.book.api.book.infra

import cube8540.book.api.book.application.BookPostRequest
import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.Publisher
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.LocalDate

internal class BookRegisterRequestBasedInitializerTest {

    private val isbn = Isbn("isbn00001")
    private val title = "title00001"
    private val publishDate = LocalDate.of(2021, 6, 2)
    private val publisher: Publisher = mockk(relaxed = true)

    private val register: BookPostRequest = mockk(relaxed = true)

    private val initializer = BookPostRequestBasedInitializer(register)

    @Nested
    inner class InitializeSeries {
        private val book = Book(isbn, title, publishDate, publisher)

        @Test
        fun `initialize when series isbn and series code is null`() {
            every { register.seriesIsbn } returns null
            every { register.seriesCode } returns null

            initializer.initializingBook(book)
            assertThat(book.series).isNull()
        }

        @Test
        fun `initialize when series isbn is null`() {
            every { register.seriesIsbn } returns null
            every { register.seriesCode } returns "seriesCode00001"

            initializer.initializingBook(book)
            assertThat(book.series!!.isbn).isEqualTo(null)
        }

        @Test
        fun `initialize series isbn`() {
            val seriesIsbn = "seriesIsbn00001"

            every { register.seriesIsbn } returns seriesIsbn

            initializer.initializingBook(book)
            assertThat(book.series!!.isbn).isEqualTo(Isbn(seriesIsbn))
        }

        @Test
        fun `initialize series code`() {
            val seriesCode = "seriesCode00001"

            every { register.seriesCode } returns seriesCode

            initializer.initializingBook(book)
            assertThat(book.series!!.code).isEqualTo(seriesCode)
        }
    }

    @Nested
    inner class InitializeThumbnail {
        private val book = Book(isbn, title, publishDate, publisher)

        @Test
        fun `initialize when thumbnail null`() {
            every { register.largeThumbnail } returns null
            every { register.mediumThumbnail } returns null
            every { register.smallThumbnail } returns null

            initializer.initializingBook(book)
            assertThat(book.thumbnail).isNull()
        }

        @Test
        fun `initialize large thumbnail`() {
            val thumbnail = URI.create("https://thumbnail")

            every { register.largeThumbnail } returns thumbnail

            initializer.initializingBook(book)
            assertThat(book.thumbnail!!.largeThumbnail).isEqualTo(thumbnail)
        }

        @Test
        fun `initialize medium thumbnail`() {
            val thumbnail = URI.create("https://thumbnail")

            every { register.mediumThumbnail } returns thumbnail

            initializer.initializingBook(book)
            assertThat(book.thumbnail!!.mediumThumbnail).isEqualTo(thumbnail)
        }

        @Test
        fun `initialize small thumbnail`() {
            val thumbnail = URI.create("https://thumbnail")

            every { register.smallThumbnail } returns thumbnail

            initializer.initializingBook(book)
            assertThat(book.thumbnail!!.smallThumbnail).isEqualTo(thumbnail)
        }
    }
}