package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.BookPostRequest
import java.beans.ConstructorProperties

data class BookRegisterRequestV1
@ConstructorProperties(value = ["requests"])
constructor(
    val requests: List<BookPostRequest>
)
