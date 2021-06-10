package cube8540.book.api.book.application

import cube8540.book.api.book.repository.PublisherRepository
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Aspect
@Component
class BookPublisherCachingSupportAOP {

    var chunkSize = 500

    @set:Autowired
    lateinit var publisherRepository: PublisherRepository

    @Transactional
    @Before(value = "execution(* cube8540.book.api.book.application.BookRegisterService.upsertBook(..)) && args(upsertRequests,..)", argNames = "upsertRequests")
    fun cachingPublisherBeforeBookUpsert(upsertRequests: List<BookPostRequest>) {
        val groupingByChunk = upsertRequests.map { it.publisherCode }.toSet().chunked(chunkSize)
        groupingByChunk.forEach { publisherRepository.findAllById(it) }
    }

}