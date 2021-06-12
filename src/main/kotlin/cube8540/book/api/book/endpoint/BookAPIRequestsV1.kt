package cube8540.book.api.book.endpoint

import java.beans.ConstructorProperties
import java.net.URI
import java.time.LocalDate

data class BookRegisterRequestV1
@ConstructorProperties(value = ["requests"])
constructor(
    var requests: List<BookPostRequestV1>
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

data class BookLookupRequestV1
@ConstructorProperties(value = [
    "publishFrom", "publishTo", "seriesIsbn", "seriesCode", "publisherCode"
])
constructor(
    var publishFrom: LocalDate?,

    var publishTo: LocalDate?,

    var seriesIsbn: String?,

    var seriesCode: String?,

    var publisherCode: String?
)