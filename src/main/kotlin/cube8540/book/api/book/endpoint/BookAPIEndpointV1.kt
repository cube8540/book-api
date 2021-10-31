package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.BookDetailsService
import cube8540.book.api.book.application.BookPageSearchService
import cube8540.book.api.book.application.BookRegisterService
import cube8540.book.api.book.domain.BookNotFoundException
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.error.ErrorMessage
import cube8540.book.api.error.ExceptionTranslator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/v1/books"])
class BookAPIEndpointV1 {

    @set:Autowired
    lateinit var bookDetailsService: BookDetailsService

    @set:[Autowired Qualifier("bookPageSearchServiceSelector")]
    lateinit var bookPageSearchService: BookPageSearchService

    @set:Autowired
    lateinit var bookRegisterService: BookRegisterService

    @set:[Autowired Qualifier("bookExceptionTranslator")]
    lateinit var translator: ExceptionTranslator<ErrorMessage<Any>>

    @set:Autowired
    lateinit var converter: BookEndpointV1Converter

    @GetMapping
    fun searchBooks(request: BookLookupRequestV1, pageable: Pageable): Page<BookDetailResponseV1> = bookPageSearchService
        .searchBooks(converter.toBookLookupCondition(request), pageable)
        .map { converter.toBookDetailsResponse(it) }

    @PostMapping
    fun registerBook(@RequestBody request: BookRegisterRequestV1): BookPostResponseV1 = bookRegisterService
        .upsertBook(request.requests.map { converter.toBookPostRequest(it) })
        .let { converter.toBookPostResponse(it) }

    @GetMapping(value = ["/{isbn}"])
    fun getBookDetails(@PathVariable isbn: String): BookDetailResponseV1 {
        val bookDetails = bookDetailsService.getBookDetails(Isbn(isbn))
            ?: throw BookNotFoundException.instance("$isbn is not found")
        val seriesList = bookDetails.series?.let { bookDetailsService.getSeriesList(it) } ?: emptyList()

        return converter.toBookDetailsResponse(bookDetails, seriesList)
    }

    @ExceptionHandler(Exception::class)
    fun handle(e: Exception): ResponseEntity<ErrorMessage<Any>> = translator.translate(e)
}