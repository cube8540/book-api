package cube8540.book.api.book.application

import cube8540.book.api.book.domain.PublisherNotFoundException
import cube8540.book.api.book.repository.PublisherRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultPublisherDetailsService(private val publisherRepository: PublisherRepository): PublisherDetailsService {

    @Transactional
    override fun existsPublisher(code: String): Boolean = publisherRepository.findById(code).isPresent

    @Transactional
    override fun loadByPublisherCode(code: String): PublisherDetails =
        publisherRepository.findDetailsByCode(code)?.let { PublisherDetails.of(it) }
            ?: throw PublisherNotFoundException.instance("$code is not found")
}