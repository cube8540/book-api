package cube8540.book.api.book.domain

import io.github.cube8540.validator.core.ValidationError
import io.github.cube8540.validator.core.ValidationRule
import io.github.cube8540.validator.core.Validator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class BookTest {

    private val isbn = Isbn("9791136202093")
    private val title = "title00001"

    private val publishDate = LocalDate.of(2021, 5, 31)

    private val publisher: Publisher = mockk(relaxed = true)

    @Nested
    inner class BookValidationTest {
        private val validatorFactory: BookValidatorFactory = mockk()
        private val book = Book(isbn, title, publishDate, publisher)

        @Test
        fun `book data is invalid throws exception`() {
            val rule: ValidationRule<Book> = mockk {
                every { isValid(book) } returns false
                every { error() } returns ValidationError("isbn", "test")
            }

            every { validatorFactory.createValidator(book) } returns Validator.of(book).registerRule(rule)

            val thrown = catchThrowable { book.isValid(validatorFactory) }
            assertThat(thrown).isInstanceOf(BookInvalidException::class.java)
        }

        @Test
        fun `book data is valid un throws exception`() {
            val rule: ValidationRule<Book> = mockk {
                every { isValid(book) } returns true
            }

            every { validatorFactory.createValidator(book) } returns Validator.of(book).registerRule(rule)

            assertThatCode { book.isValid(validatorFactory) }.doesNotThrowAnyException()
        }

        @Test
        fun `book validation results returns`() {
            val rule: ValidationRule<Book> = mockk {
                every { isValid(book) } returns false
                every { error() } returns ValidationError("isbn", "test")
            }

            every { validatorFactory.createValidator(book) } returns Validator.of(book).registerRule(rule)

            val result = book.validationResult(validatorFactory)
            assertThat(result.hasError()).isTrue
            assertThat(result.errors).containsExactly(ValidationError("isbn", "test"))
        }
    }

    @Nested
    inner class BookMergeTest {
        private val bookBase = Book(isbn, title, publishDate, publisher)

        @Test
        fun `book change title`() {
            val title = "mergeTitle00001"
            val book: Book = mockk(relaxed = true)

            every { book.title } returns title

            bookBase.mergeBook(book)
            assertThat(bookBase.title).isEqualTo(title)
        }

        @Test
        fun `book change publish date`() {
            val publishDate: LocalDate = mockk(relaxed = true)
            val book: Book = mockk(relaxed = true)

            every { book.publishDate } returns publishDate

            bookBase.mergeBook(book)
            assertThat(bookBase.publishDate).isEqualTo(publishDate)
        }

        @Test
        fun `book change description when given description is null`() {
            val originalDescription = "originalDescription00001"

            val description = null
            val book: Book = mockk(relaxed = true)

            bookBase.description = originalDescription
            every { book.description } returns description

            bookBase.mergeBook(book)
            assertThat(bookBase.description).isEqualTo(originalDescription)
        }

        @Test
        fun `book change description`() {
            val originalDescription = "originalDescription00001"

            val description = "description00001"
            val book: Book = mockk(relaxed = true)

            bookBase.description = originalDescription
            every { book.description } returns description

            bookBase.mergeBook(book)
            assertThat(bookBase.description).isEqualTo(description)
        }

        @Test
        fun `book change index when given index is null`() {
            val originalIndex = mutableListOf("originalIndex 0000")

            val index = null
            val book: Book = mockk(relaxed = true)

            bookBase.indexes = originalIndex
            every { book.indexes } returns index

            bookBase.mergeBook(book)
            assertThat(bookBase.indexes).isEqualTo(originalIndex)
        }

        @Test
        fun `book change index when given index is equal original index`() {
            val originalIndex = mutableListOf("originalIndex 0000")

            val index = mutableListOf("originalIndex 0000")
            val book: Book = mockk(relaxed = true)

            bookBase.indexes = originalIndex
            every { book.indexes } returns index

            bookBase.mergeBook(book)
            assertThat(bookBase.indexes === index).isFalse
        }

        @Test
        fun `book change index`() {
            val originalIndex = mutableListOf("originalIndex 0000")

            val index = mutableListOf("mergeIndex 0000")
            val book: Book = mockk(relaxed = true)

            bookBase.indexes = originalIndex
            every { book.indexes } returns index

            bookBase.mergeBook(book)
            assertThat(bookBase.indexes).isEqualTo(index)
        }

        @Test
        fun `book change series when given series is null`() {
            val series = null
            val originalSeries: Series = mockk(relaxed = true)

            val book: Book = mockk(relaxed = true)

            bookBase.series = originalSeries
            every { book.series } returns series

            bookBase.mergeBook(book)
            assertThat(bookBase.series).isEqualTo(originalSeries)
        }

        @Test
        fun `book change series when original series is null`() {
            val originalSeries = null
            val series: Series = mockk(relaxed = true)

            val book: Book = mockk(relaxed = true)

            bookBase.series = originalSeries
            every { book.series } returns series

            bookBase.mergeBook(book)
            assertThat(bookBase.series).isEqualTo(series)
        }

        @Test
        fun `book merge series`() {
            val originalSeries: Series = mockk(relaxed = true)
            val series: Series = mockk(relaxed = true)

            val book: Book = mockk(relaxed = true)

            bookBase.series = originalSeries
            every { book.series } returns series

            bookBase.mergeBook(book)
            verify { originalSeries.mergeSeries(series) }
        }

        @Test
        fun `book change thumbnail when given thumbnail is null`() {
            val thumbnail = null
            val originalThumbnail: BookThumbnail = mockk(relaxed = true)

            val book: Book = mockk(relaxed = true)

            bookBase.thumbnail = originalThumbnail
            every { book.thumbnail } returns thumbnail

            bookBase.mergeBook(book)
            assertThat(bookBase.thumbnail).isEqualTo(originalThumbnail)
        }

        @Test
        fun `book change thumbnail when original thumbnail is null`() {
            val thumbnail: BookThumbnail = mockk(relaxed = true)
            val originalThumbnail = null

            val book: Book = mockk(relaxed = true)

            bookBase.thumbnail = originalThumbnail
            every { book.thumbnail } returns thumbnail

            bookBase.mergeBook(book)
            assertThat(bookBase.thumbnail).isEqualTo(thumbnail)
        }

        @Test
        fun `book merge thumbnail`() {
            val thumbnail: BookThumbnail = mockk(relaxed = true)
            val originalThumbnail: BookThumbnail = mockk(relaxed = true)

            val book: Book = mockk(relaxed = true)

            bookBase.thumbnail = originalThumbnail
            every { book.thumbnail } returns thumbnail

            bookBase.mergeBook(book)
            verify { originalThumbnail.mergeThumbnail(thumbnail) }
        }

        @Test
        fun `book merge authors when given authors is null`() {
            val authors = null
            val originalAuthors: MutableSet<String> = mockk(relaxed = true)

            val book: Book = mockk(relaxed = true)

            bookBase.authors = originalAuthors
            every { book.authors } returns authors

            bookBase.mergeBook(book)
            assertThat(bookBase.authors).isEqualTo(originalAuthors)
        }

        @Test
        fun `book merge authors when original authors is null`() {
            val authors: MutableSet<String> =  mockk(relaxed = true) {
                every { isNotEmpty() } returns true
                every { isEmpty() } returns false
            }
            val originalAuthors = null

            val book: Book = mockk(relaxed = true)

            bookBase.authors = originalAuthors
            every { book.authors } returns authors

            bookBase.mergeBook(book)
            assertThat(bookBase.authors).isEqualTo(authors)
        }

        @Test
        fun `book merge authors when request data is equals to original`() {
            val authors: MutableSet<String> = mutableSetOf("authors")
            val originalAuthors: MutableSet<String> = mutableSetOf("authors")

            val book: Book = mockk(relaxed = true)

            bookBase.authors = originalAuthors
            every { book.authors } returns authors

            bookBase.mergeBook(book)
            assertThat(bookBase.authors).isEqualTo(originalAuthors)
            assertThat(bookBase.authors === originalAuthors).isTrue
        }

        @Test
        fun `book merge authors`() {
            val authors: MutableSet<String> = mutableSetOf("mergeAuthority")
            val originalAuthors: MutableSet<String> = mutableSetOf("originalAuthority")

            val book: Book = mockk(relaxed = true)

            bookBase.authors = originalAuthors
            every { book.authors } returns authors

            bookBase.mergeBook(book)
            assertThat(bookBase.authors).isEqualTo(authors + originalAuthors)
        }
        
        @Test
        fun `book merge external link when given external link is null`() {
            val externalLinks = null
            val originalExternalLinks: MutableMap<MappingType, BookExternalLink> = mockk(relaxed = true)

            val book: Book = mockk(relaxed = true)

            bookBase.externalLinks = originalExternalLinks
            every { book.externalLinks } returns externalLinks

            bookBase.mergeBook(book)
            assertThat(bookBase.externalLinks).isEqualTo(originalExternalLinks)
        }

        @Test
        fun `book merge external link when original external link is null`() {
            val externalLinks = mutableMapOf(MappingType.KYOBO to BookExternalLink(defaultLinkUri, defaultOriginalPrice, defaultSalePrice))

            val book: Book = mockk(relaxed = true)

            bookBase.externalLinks = null
            every { book.externalLinks } returns externalLinks

            bookBase.mergeBook(book)
            assertThat(bookBase.externalLinks).isEqualTo(externalLinks)
        }

        @Test
        fun `book merge external link`() {
            val externalLinks = mutableMapOf(MappingType.KYOBO to BookExternalLink(defaultLinkUri, defaultOriginalPrice, defaultSalePrice))
            val originalExternalLinks = mutableMapOf(MappingType.ALADIN to BookExternalLink(defaultLinkUri, defaultOriginalPrice, defaultSalePrice))

            val book: Book = mockk(relaxed = true)

            bookBase.externalLinks = originalExternalLinks
            every { book.externalLinks } returns externalLinks

            bookBase.mergeBook(book)
            assertThat(bookBase.externalLinks).isEqualTo(externalLinks + originalExternalLinks)
        }
    }

}