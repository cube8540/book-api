package cube8540.book.api.book.application

import cube8540.book.api.book.domain.*
import java.net.URI
import java.time.LocalDate

val publisherDetailsAssertIgnoreFields = listOf("createdAt", "updatedAt").toTypedArray()

fun createBookPostRequest(
    isbn: String,
    title: String = defaultTitle,
    publisherCode: String = defaultPublisherCode,
    publishDate: LocalDate = defaultPublishDate,
    seriesIsbn: String? = defaultSeriesIsbn,
    seriesCode: String? = defaultSeriesCode,
    largeThumbnail: URI? = defaultLargeThumbnail,
    mediumThumbnail: URI? = defaultMediumThumbnail,
    smallThumbnail: URI? = defaultSmallThumbnail,
    authors: MutableSet<String>? = defaultAuthors,
    description: String? = defaultDescription,
    price: Double? = defaultPrice
): BookPostRequest = BookPostRequest(
    isbn = isbn,
    title = title,
    publishDate = publishDate,
    publisherCode = publisherCode,
    seriesIsbn = seriesIsbn,
    seriesCode = seriesCode,
    largeThumbnail = largeThumbnail,
    mediumThumbnail = mediumThumbnail,
    smallThumbnail = smallThumbnail,
    authors = authors,
    description = description,
    price = price
)

fun createPublisherDetails(
    code: String,
    name: String? = defaultPublisherName
): PublisherDetails = PublisherDetails(code = code, name = name)