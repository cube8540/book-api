package cube8540.book.api.book.application

import cube8540.book.api.book.domain.createSeries
import cube8540.book.api.book.domain.createThumbnail
import cube8540.book.api.book.domain.defaultAuthors
import cube8540.book.api.book.domain.defaultDescription
import cube8540.book.api.book.domain.defaultIndexes
import cube8540.book.api.book.domain.defaultLargeThumbnail
import cube8540.book.api.book.domain.defaultMediumThumbnail
import cube8540.book.api.book.domain.defaultPrice
import cube8540.book.api.book.domain.defaultPublishDate
import cube8540.book.api.book.domain.defaultPublisherCode
import cube8540.book.api.book.domain.defaultPublisherName
import cube8540.book.api.book.domain.defaultSeriesCode
import cube8540.book.api.book.domain.defaultSeriesIsbn
import cube8540.book.api.book.domain.defaultSmallThumbnail
import cube8540.book.api.book.domain.defaultTitle
import cube8540.book.api.book.repository.BookQueryCondition
import java.net.URI
import java.time.LocalDate
import org.springframework.data.domain.Sort

val defaultPublishFrom: LocalDate = LocalDate.of(2021, 6, 1)
val defaultPublishTo: LocalDate = LocalDate.of(2021, 6, 30)

val bookDetailsAssertIgnoreFields = listOf(BookDetails::createdAt.name, BookDetails::updatedAt.name).toTypedArray()
val publisherDetailsAssertIgnoreFields = listOf(PublisherDetails::createdAt.name, PublisherDetails::updatedAt.name).toTypedArray()

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
    indexes: MutableList<String>? = defaultIndexes,
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
    indexes = indexes,
    price = price
)

fun createBookLookupCondition(
    publisherCode: String? = defaultPublisherCode,
    publishFrom: LocalDate? = defaultPublishFrom,
    publishTo: LocalDate? = defaultPublishTo,
    seriesIsbn: String? = defaultSeriesIsbn,
    seriesCode: String? = defaultSeriesCode,
    title: String? = defaultTitle,
    direction: Sort.Direction = Sort.Direction.DESC
): BookLookupCondition = BookLookupCondition(
    publishFrom = publishFrom,
    publishTo = publishTo,
    publisherCode = publisherCode,
    seriesIsbn = seriesIsbn,
    seriesCode = seriesCode,
    title = title,
    direction = direction
)

fun createBookQueryCondition(
    publisherCode: String? = defaultPublisherCode,
    publishFrom: LocalDate? = defaultPublishFrom,
    publishTo: LocalDate? = defaultPublishTo,
    seriesIsbn: String? = defaultSeriesIsbn,
    seriesCode: String? = defaultSeriesCode
): BookQueryCondition = BookQueryCondition(
    publishFrom = publishFrom,
    publishTo = publishTo,
    publisherCode = publisherCode,
    seriesIsbn = seriesIsbn,
    seriesCode = seriesCode
)

fun createBookDetails(
    isbn: String,
    title: String = defaultTitle,
    publisherCode: String = defaultPublisherCode,
    publisherName: String? = defaultPublisherName,
    publishDate: LocalDate = defaultPublishDate,
    seriesIsbn: String? = defaultSeriesIsbn,
    seriesCode: String? = defaultSeriesCode,
    largeThumbnail: URI? = defaultLargeThumbnail,
    mediumThumbnail: URI? = defaultMediumThumbnail,
    smallThumbnail: URI? = defaultSmallThumbnail,
    authors: MutableSet<String>? = defaultAuthors,
    description: String? = defaultDescription,
    indexes: MutableList<String>? = defaultIndexes,
    price: Double? = defaultPrice
): BookDetails = BookDetails(
    isbn = isbn,
    title = title,
    publisher = createPublisherDetails(publisherCode, publisherName),
    publishDate = publishDate,
    series = createSeries(seriesIsbn, seriesCode),
    thumbnail = createThumbnail(largeThumbnail, mediumThumbnail, smallThumbnail),
    authors = authors,
    description = description,
    indexes = indexes,
    price = price
)

fun createPublisherDetails(
    code: String = defaultPublisherCode,
    name: String? = defaultPublisherName
): PublisherDetails = PublisherDetails(code = code, name = name)