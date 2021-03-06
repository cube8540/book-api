package cube8540.book.api.book.endpoint

import cube8540.book.api.book.domain.MappingType
import java.beans.ConstructorProperties
import java.net.URI
import java.time.LocalDate
import org.springframework.data.domain.Sort

data class BookRegisterRequestV1
@ConstructorProperties(value = ["requests"])
constructor(
    var requests: List<BookPostRequestV1>
)

data class BookExternalLinkRegisterRequestV1
@ConstructorProperties(value = ["productDetailPage", "originalPrice", "salePrice"])
constructor(
    var productDetailPage: URI,

    var originalPrice: Double?,

    var salePrice: Double?
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
    "indexes",
    "externalLinks",
    "confirmedPublication"
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

    var indexes: MutableList<String>?,

    var externalLinks: MutableMap<MappingType, BookExternalLinkRegisterRequestV1>?,

    var confirmedPublication: Boolean?
)

data class BookLookupRequestV1
@ConstructorProperties(value = [
    "publishFrom", "publishTo", "seriesIsbn", "seriesCode", "publisherCode", "title", "direction"
])
constructor(
    var publishFrom: LocalDate?,

    var publishTo: LocalDate?,

    var seriesIsbn: String?,

    var seriesCode: String?,

    var publisherCode: String?,

    var title: String?,

    var direction: Sort.Direction? = Sort.Direction.DESC
)