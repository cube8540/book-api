package cube8540.book.api.book.application

interface PublisherDetailsService {
    fun existsPublisher(code: String): Boolean

    fun loadAllPublishers(): List<PublisherDetails>

    fun loadByPublisherCode(code: String): PublisherDetails
}