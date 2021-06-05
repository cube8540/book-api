package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.BookRegisterService
import cube8540.book.api.error.ErrorMessage
import cube8540.book.api.error.ExceptionTranslator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(value = ["/api/v1/books"])
class BookAPIEndpointV1 {

    @set:Autowired
    lateinit var bookRegisterService: BookRegisterService

    @set:[Autowired Qualifier("bookExceptionTranslator")]
    lateinit var translator: ExceptionTranslator<ErrorMessage<Any>>

    @PostMapping
    fun registerBook(@RequestBody request: BookRegisterRequestV1): Map<String, String> {
        bookRegisterService.upsertBook(request.requests)

        return Collections.singletonMap("status", "ok")
    }

    @ExceptionHandler(Exception::class)
    fun handle(e: Exception): ResponseEntity<ErrorMessage<Any>> = translator.translate(e)
}