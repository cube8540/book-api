package cube8540.book.api.book.application

import cube8540.book.api.book.repository.PublisherContainerHolder
import cube8540.book.api.book.repository.PublisherRepository
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Aspect
@Component
class BookPublisherCachingSupportAOP @Autowired constructor(private val publisherRepository: PublisherRepository) {

    companion object {
        const val defaultChunkSize = 500
    }

    var chunkSize = defaultChunkSize

    @Transactional
    @Around(value = "execution(* cube8540.book.api.book.application.BookRegisterService.upsertBook(..)) && args(upsertRequests,..)", argNames = "upsertRequests")
    fun cachingPublisherBeforeBookUpsert(joinPoint: ProceedingJoinPoint, upsertRequests: List<BookPostRequest>): Any {
        val groupingByChunk = upsertRequests.map { it.publisherCode }.toSet().chunked(chunkSize)
        groupingByChunk.forEach { PublisherContainerHolder.getContainer().storeAll(publisherRepository.findAllById(it)) }

        try {
            return joinPoint.proceed()
        } finally {
            PublisherContainerHolder.clearContainer()
        }
    }
}