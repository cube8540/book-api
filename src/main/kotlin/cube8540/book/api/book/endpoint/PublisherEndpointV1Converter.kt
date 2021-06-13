package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.PublisherDetails
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PublisherEndpointV1Converter {

    fun toPublisherResponse(response: PublisherDetails): PublisherResponseV1
}