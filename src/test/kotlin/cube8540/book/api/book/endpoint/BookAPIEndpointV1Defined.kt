package cube8540.book.api.book.endpoint

import cube8540.book.api.book.domain.defaultAuthors
import cube8540.book.api.book.domain.defaultDescription
import cube8540.book.api.book.domain.defaultLargeThumbnail
import cube8540.book.api.book.domain.defaultPrice
import cube8540.book.api.book.domain.defaultPublishDate
import cube8540.book.api.book.domain.defaultPublisherCode
import cube8540.book.api.book.domain.defaultPublisherName
import cube8540.book.api.book.domain.defaultSeriesCode
import cube8540.book.api.book.domain.defaultSeriesIsbn
import cube8540.book.api.book.domain.defaultTitle
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime

fun createBookDetailsResponseV1(
    isbn: String,
    title: String = defaultTitle,
    publishDate: LocalDate = defaultPublishDate,
    publisherCode: String = defaultPublisherCode,
    publisherName: String? = defaultPublisherName,
    seriesIsbn: String? = defaultSeriesIsbn,
    seriesCode: String? = defaultSeriesCode,
    largeThumbnail: URI? = defaultLargeThumbnail,
    mediumThumbnail: URI? = defaultLargeThumbnail,
    smallThumbnail: URI? = defaultLargeThumbnail,
    authors: Set<String>? = defaultAuthors,
    description: String? = defaultDescription,
    createdAt: LocalDateTime? = null,
    updatedAt: LocalDateTime? = null,
    price: Double? = defaultPrice,
    seriesList: List<BookDetailsResponseV1>? = emptyList()
) = BookDetailsResponseV1(isbn, title, publishDate, publisherCode, publisherName, seriesIsbn, seriesCode,
    largeThumbnail, mediumThumbnail, smallThumbnail, authors, description, price, createdAt, updatedAt, seriesList)