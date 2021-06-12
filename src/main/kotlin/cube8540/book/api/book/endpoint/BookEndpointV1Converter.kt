package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.BookPostRequest
import org.mapstruct.Mapper

@Mapper
interface BookEndpointV1Converter {

    fun toBookPostRequest(request: BookPostRequestV1): BookPostRequest

}