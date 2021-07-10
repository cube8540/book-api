package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.BookDetailsService
import cube8540.book.api.book.application.createBookDetails
import cube8540.book.api.book.domain.BookNotFoundException
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.Series
import cube8540.book.api.book.domain.defaultSeriesCode
import cube8540.book.api.book.domain.defaultSeriesIsbn
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class BookAPIEndpointV1Test {

    val bookDetailsService: BookDetailsService = mockk()

    val converter: BookEndpointV1Converter = mockk()

    val endpoint = BookAPIEndpointV1()

    init {
        endpoint.bookDetailsService = bookDetailsService
        endpoint.converter = converter
    }

    @Nested
    inner class GetBookDetails {

        @Test
        fun `get book details if service returns null`() {
            val isbn = "isbn0000"

            every { bookDetailsService.getBookDetails(Isbn(isbn)) } returns null

            assertThatThrownBy { endpoint.getBookDetails(isbn) }
                .isInstanceOf(BookNotFoundException::class.java)
        }

        @Test
        fun `get book details if service returns not null`() {
            val isbn = "isbn0000"
            val series = Series(isbn = Isbn(defaultSeriesIsbn), code = defaultSeriesCode)

            val bookDetails = createBookDetails(isbn = isbn, seriesIsbn = defaultSeriesIsbn, seriesCode = defaultSeriesCode)
            val seriesList = listOf(
                createBookDetails(isbn = "isbn0001"),
                createBookDetails(isbn = "isbn0002"),
                createBookDetails(isbn = "isbn0003"),
            )
            val response = createBookDetailsResponseV1(isbn = isbn, seriesList = listOf(
                createBookDetailsResponseV1(isbn = "isbn0001"),
                createBookDetailsResponseV1(isbn = "isbn0002"),
                createBookDetailsResponseV1(isbn = "isbn0003")
            ))

            every { bookDetailsService.getBookDetails(Isbn(isbn)) } returns bookDetails
            every { bookDetailsService.getSeriesList(series) } returns seriesList
            every { converter.toBookDetailsResponse(bookDetails, seriesList) } returns response

            val result = endpoint.getBookDetails(isbn)
            assertThat(result).isEqualTo(response)
        }
    }
}