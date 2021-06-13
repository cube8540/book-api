package cube8540.book.api.book.repository

import cube8540.book.api.book.domain.Publisher
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

interface PublisherCustomRepository {
    fun findDetailsByCode(code: String): Publisher?

    fun findAll(sort: Sort): List<Publisher>
}

interface PublisherRepository: JpaRepository<Publisher, String>, PublisherCustomRepository