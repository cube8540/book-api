 package cube8540.book.api.book.application

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

    var price: Double?
)