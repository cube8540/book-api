package cube8540.book.api.book.endpoint

import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime

data class BookResponseV1(
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
    var price: Double?,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?
)