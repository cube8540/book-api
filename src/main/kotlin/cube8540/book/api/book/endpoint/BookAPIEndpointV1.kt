package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.BookPageSearchService
import cube8540.book.api.book.application.BookRegisterService
import cube8540.book.api.error.ErrorMessage
import cube8540.book.api.error.ExceptionTranslator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/api/v1/books"])
class BookAPIEndpointV1 {

    @set:Autowired
    lateinit var bookPageSearchService: BookPageSearchService

    @set:Autowired
    lateinit var bookRegisterService: BookRegisterService

    @set:[Autowired Qualifier("bookExceptionTranslator")]
    lateinit var translator: ExceptionTranslator<ErrorMessage<Any>>

    @set:Autowired
    lateinit var converter: BookEndpointV1Converter

    @GetMapping
    fun lookupBooks(request: BookLookupRequestV1, pageable: Pageable): Page<BookPageResponseV1> = bookPageSearchService
        .lookupBooks(converter.toBookLookupCondition(request), pageable)
        .map { converter.toBookPageResponse(it) }

    @PostMapping
    fun registerBook(@RequestBody request: BookRegisterRequestV1): BookPostResponseV1 = bookRegisterService
        .upsertBook(request.requests.map { converter.toBookPostRequest(it) })
        .let { converter.toBookPostResponse(it) }

    @ExceptionHandler(Exception::class)
    fun handle(e: Exception): ResponseEntity<ErrorMessage<Any>> = translator.translate(e)
}