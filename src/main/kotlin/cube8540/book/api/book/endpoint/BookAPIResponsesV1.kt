package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.BookPostErrorReason
import cube8540.book.api.book.domain.MappingType
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime

data class BookPostResponseV1(
    var successBooks: List<String> = emptyList(),
    var failedBooks: List<BookPostErrorReason> = emptyList()
)

data class BookExternalLinkResponseV1(
    var productDetailPage: URI,
    var originalPrice: Double? = null,
    var salePrice: Double? = null
)

data class BookDetailResponseV1(
    var isbn: String,
    var title: String,
    var publishDate: LocalDate,
    var publisherCode: String,
    var publisherName: String?,
    var seriesIsbn: String?,
    var seriesCode: String?,
    var largeThumbnail: URI?,
    var mediumThumbnail: URI?,
    var smallThumbnail: URI?,
    var authors: Set<String>?,
    var description: String?,
    var indexes: List<String>?,
    var externalLinks: Map<MappingType, BookExternalLinkResponseV1>? = null,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
    var seriesList: List<BookDetailResponseV1>?
)