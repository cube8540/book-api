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

    fun toBookPostResponse(response: BookPostResult): BookPostResponseV1

    @Mappings(value = [
        Mapping(source = "publisher.code", target = "publisherCode"),
        Mapping(source = "publisher.name", target = "publisherName"),
        Mapping(source = "series.isbn.value", target = "seriesIsbn"),
        Mapping(source = "series.code", target = "seriesCode"),
        Mapping(source = "thumbnail.largeThumbnail", target = "largeThumbnail"),
        Mapping(source = "thumbnail.mediumThumbnail", target = "mediumThumbnail"),
        Mapping(source = "thumbnail.smallThumbnail", target = "smallThumbnail"),
        Mapping(target = "seriesList", ignore = true)
    ])
    fun toBookDetailsResponse(bookDetails: BookDetails): BookDetailsResponseV1

    @Mappings(value = [
        Mapping(source = "bookDetails.publisher.code", target = "publisherCode"),
        Mapping(source = "bookDetails.publisher.name", target = "publisherName"),
        Mapping(source = "bookDetails.series.isbn.value", target = "seriesIsbn"),
        Mapping(source = "bookDetails.series.code", target = "seriesCode"),
        Mapping(source = "bookDetails.thumbnail.largeThumbnail", target = "largeThumbnail"),
        Mapping(source = "bookDetails.thumbnail.mediumThumbnail", target = "mediumThumbnail"),
        Mapping(source = "bookDetails.thumbnail.smallThumbnail", target = "smallThumbnail"),
        Mapping(source = "seriesList", target = "seriesList")
    ])
    fun toBookDetailsResponse(bookDetails: BookDetails, seriesList: List<BookDetails>): BookDetailsResponseV1

}