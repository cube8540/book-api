package cube8540.book.api.book.application

import cube8540.book.api.book.domain.MappingType
import java.net.URI
import java.time.LocalDate

data class BookPostRequest(
    var isbn: String,

    var title: String,

    var publishDate: LocalDate,

    var publisherCode: String,

    var seriesIsbn: String?,

    var seriesCode: String?,

    var largeThumbnail: URI?,

    var mediumThumbnail: URI?,

    var smallThumbnail: URI?,

    var authors: MutableSet<String>?,

    var description: String?,

    var indexes: MutableList<String>?,

    var externalLinks: MutableMap<MappingType, BookExternalLinkPostRequest>?
)

data class BookExternalLinkPostRequest(
    var productDetailPage: URI,

    var originalPrice: Double?,

    var salePrice: Double?
)

data class BookPostResult(
    var successBooks: List<String>,
    var failedBooks: List<BookPostErrorReason>
)

interface BookRegisterService {
    fun upsertBook(upsertRequests: List<BookPostRequest>): BookPostResult
}