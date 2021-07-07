package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.BookDetails
import cube8540.book.api.book.application.BookLookupCondition
import cube8540.book.api.book.application.BookPostRequest
import cube8540.book.api.book.application.BookPostResult
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface BookEndpointV1Converter {

    fun toBookPostRequest(request: BookPostRequestV1): BookPostRequest

    fun toBookLookupCondition(request: BookLookupRequestV1): BookLookupCondition

    @Mappings(value = [
        Mapping(source = "publisher.code", target = "publisherCode"),
        Mapping(source = "publisher.name", target = "publisherName"),
        Mapping(source = "series.isbn.value", target = "seriesIsbn"),
        Mapping(source = "series.code", target = "seriesCode"),
        Mapping(source = "thumbnail.largeThumbnail", target = "largeThumbnail"),
        Mapping(source = "thumbnail.mediumThumbnail", target = "mediumThumbnail"),
        Mapping(source = "thumbnail.smallThumbnail", target = "smallThumbnail"),
    ])
    fun toBookPageResponse(response: BookDetails): BookPageResponseV1

    fun toBookPostResponse(response: BookPostResult): BookPostResponseV1
}