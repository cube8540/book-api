package cube8540.book.api.book.domain

import io.mockk.every
import io.mockk.mockk
import java.net.URI
import java.time.LocalDate

const val defaultTitle = "title0000"

val defaultPublishDate: LocalDate = LocalDate.of(2021, 6, 2)

const val defaultPublisherCode = "publisher0000"
const val defaultPublisherName = "publisherName00000"

const val defaultSeriesIsbn = "seriesIsbn00000"
const val defaultSeriesCode = "seriesCode0000"

val defaultLargeThumbnail: URI = URI.create("http://large-thumbnail")
val defaultMediumThumbnail: URI = URI.create("http://mediumThubmnail")
val defaultSmallThumbnail: URI = URI.create("http://small-thumbnail")

val defaultAuthors = emptySet<String>().toMutableSet()
const val defaultDescription = "description00000"
const val defaultPrice = 5000.0

var bookAssertIgnoreFields = listOf(Book::createdAt.name, Book::updatedAt.name).toTypedArray()

fun createBook(
    isbn: String,
    title: String = defaultTitle,
    publisherCode: String = defaultPublisherCode,
    publisher: Publisher? = null,
    publishDate: LocalDate = defaultPublishDate,
    seriesIsbn: String? = defaultSeriesIsbn,
    seriesCode: String? = defaultSeriesCode,
    largeThumbnail: URI? = defaultLargeThumbnail,
    mediumThumbnail: URI? = defaultMediumThumbnail,
    smallThumbnail: URI? = defaultSmallThumbnail,
    authors: MutableSet<String>? = defaultAuthors,
    description: String? = defaultDescription,
    price: Double? = defaultPrice,
    newObject: Boolean = true
): Book {
    val book = Book(
        isbn = Isbn(isbn),
        title = title,
        publisher = publisher ?: Publisher.fake(publisherCode),
        publishDate = publishDate
    )

    book.series = createSeries(seriesIsbn, seriesCode)
    book.thumbnail = createThumbnail(largeThumbnail, mediumThumbnail, smallThumbnail)
    book.authors = authors
    book.description = description
    book.price = price

    if (!newObject) {
        book.markingPersistedEntity()
    }

    return book
}

fun createPublisher(
    code: String = defaultPublisherCode,
    name: String? = defaultPublisherName
): Publisher {
    val codeGenerator: PublisherCodeGenerator = mockk(relaxed = true) {
        every { generateCode() } returns code
    }
    val publisher = Publisher(codeGenerator)

    publisher.name = name
    return publisher
}

fun createSeries(seriesIsbn: String?, seriesCode: String?): Series? =
    if (seriesIsbn == null && seriesCode == null) {
        null
    } else {
        Series(seriesIsbn?.let { Isbn(it) }, seriesCode)
    }

fun createThumbnail(large: URI?, medium: URI?, small: URI?): BookThumbnail? =
    if (large == null && medium == null && small == null) {
        null
    } else {
        BookThumbnail(large, medium, small)
    }

class BookTestException: RuntimeException()