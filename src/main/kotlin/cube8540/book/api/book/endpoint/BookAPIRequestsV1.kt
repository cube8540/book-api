package cube8540.book.api.book.endpoint

import java.beans.ConstructorProperties
import java.net.URI
import java.time.LocalDate

data class BookRegisterRequestV1
@ConstructorProperties(value = ["requests"])
constructor(
    val requests: List<BookPostRequestV1>
)

data class BookPostRequestV1
@ConstructorProperties(value = [
    "isbn",
    "title",
    "publishDate",
    "publisherCode",
    "seriesIsbn",
    "seriesCode",
    "largeThumbnail",
    "mediumThumbnail",
    "smallThumbnail",
    "authors",
    "description",
    "price"
])
constructor(
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