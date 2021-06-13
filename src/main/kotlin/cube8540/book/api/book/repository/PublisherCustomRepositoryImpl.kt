package cube8540.book.api.book.repository

import cube8540.book.api.book.domain.Publisher
import cube8540.book.api.book.domain.QPublisher
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class PublisherCustomRepositoryImpl: PublisherCustomRepository, QuerydslRepositorySupport(Publisher::class.java) {

    val publisher: QPublisher = QPublisher.publisher

    override fun findDetailsByCode(code: String): Publisher? = from(publisher)
        .where(publisher.code.eq(code))
        .fetchOne()

    override fun findAll(sort: Sort): List<Publisher> {
        val queryExpression = from(publisher)

        querydsl!!.applySorting(sort, queryExpression)
        return queryExpression.fetch()
    }
}