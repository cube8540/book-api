 package cube8540.book.api.book.application

import java.net.URI
import java.time.LocalDate

data class BookPostRequest(
    val isbn: String,

    val title: String,

    val publishDate: LocalDate,

    val publisherCode: String,

    val seriesIsbn: String?,

    val seriesCode: String?,

    val largeThumbnail: URI?,

    val mediumThumbnail: URI?,

    val smallThumbnail: URI?,

    val authors: MutableSet<String>?,

    val description: String?,

    val price: Double?
)