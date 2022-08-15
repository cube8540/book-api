package cube8540.book.api.book.repository

import cube8540.book.api.book.domain.Publisher

interface PublisherContainer {
    fun store(publisher: Publisher)

    fun storeAll(publishers: Collection<Publisher>)

    fun getPublisher(code: String): Publisher?

    fun isEmpty(): Boolean
}

class DefaultPublisherContainer: PublisherContainer {

    private val map: MutableMap<String, Publisher> = HashMap()

    override fun store(publisher: Publisher) {
        map[publisher.code] = publisher
    }

    override fun storeAll(publishers: Collection<Publisher>) {
        publishers.forEach { store(it) }
    }

    override fun getPublisher(code: String): Publisher? = map[code]

    override fun isEmpty(): Boolean = map.isEmpty()
}