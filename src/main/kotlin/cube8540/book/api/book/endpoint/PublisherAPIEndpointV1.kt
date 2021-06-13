package cube8540.book.api.book.endpoint

import cube8540.book.api.book.application.PublisherDetailsService
import cube8540.book.api.error.ErrorMessage
import cube8540.book.api.error.ExceptionTranslator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/v1/publishers"])
class PublisherAPIEndpointV1 {

    @set:Autowired
    lateinit var publisherDetailsService: PublisherDetailsService

    @set:[Autowired Qualifier("publisherExceptionTranslator")]
    lateinit var translator: ExceptionTranslator<ErrorMessage<Any>>

    @set:Autowired
    lateinit var converter: PublisherEndpointV1Converter

    @GetMapping
    fun getPublishers(): Map<String, List<PublisherResponseV1>> {
        val results = publisherDetailsService.loadAllPublishers()
        return mapOf("results" to results.map { converter.toPublisherResponse(it) })
    }

    @ExceptionHandler(Exception::class)
    fun handle(e: Exception): ResponseEntity<ErrorMessage<Any>> = translator.translate(e)

}