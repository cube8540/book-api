package cube8540.book.api.book.repository

import com.querydsl.core.types.dsl.BooleanExpression
import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.QBook
import cube8540.book.api.book.domain.Series
import java.time.LocalDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class BookCustomRepositoryImpl : BookCustomRepository, QuerydslRepositorySupport(Book::class.java) {

    val book: QBook = QBook.book

    override fun findDetailsByIsbn(isbn: Isbn): Book? = from(book)
        .leftJoin(book.publisher).fetchJoin()
        .where(book.isbn.eq(isbn))
        .fetchOne()

    override fun findDetailsByIsbn(isbnList: Collection<Isbn>): List<Book> =
        if (isbnList.isNotEmpty()) {
            from(book)
                .leftJoin(book.publisher).fetchJoin()
                .where(book.isbn.`in`(isbnList))
                .fetch()
        } else {
            emptyList()
        }

    override fun findSeries(series: Series): List<Book> {
        return from(book)
            .leftJoin(book.publisher).fetchJoin()
            .where(seriesEqual(series.isbn?.value, series.code))
            .orderBy(book.publishDate.asc())
            .fetch()
    }

    override fun findPageByCondition(condition: BookQueryCondition, pageRequest: PageRequest): Page<Book> {
        val queryExpression = from(book)
            .leftJoin(book.publisher).fetchJoin()
            .where(
                publishDateBetween(condition.publishFrom, condition.publishTo),
                seriesEqual(condition.seriesIsbn, condition.seriesCode),
                publisherEqual(condition.publisherCode)
            )
        querydsl!!.applyPagination(pageRequest, queryExpression)

        val queryResults = queryExpression.fetchResults()

        return PageImpl(queryResults.results, pageRequest, queryResults.total)
    }

    override fun findByPublishDate(date: LocalDate, pageRequest: PageRequest): Page<Book> {
        val queryExpression = from(book)
            .leftJoin(book.publisher).fetchJoin()
            .where(publishDateBetween(date, date))
        querydsl!!.applyPagination(pageRequest, queryExpression)

        val queryResults = queryExpression.fetchResults()

        return PageImpl(queryResults.results, pageRequest, queryResults.total)
    }

    override fun findAll(pageRequest: PageRequest): Page<Book> {
        val queryExpression = from(book)
            .leftJoin(book.publisher).fetchJoin()
        querydsl!!.applyPagination(pageRequest, queryExpression)

        val queryResults = queryExpression.fetchResults()

        return PageImpl(queryResults.results, pageRequest, queryResults.total)
    }

    private fun publishDateBetween(from: LocalDate?, to: LocalDate?): BooleanExpression? {
        return if (from != null && to != null) {
            book.publishDate.between(from, to)
        } else if (from != null) {
            book.publishDate.loe(from)
        } else if (to != null) {
            book.publishDate.goe(to)
        } else {
            null
        }
    }

    private fun seriesEqual(seriesIsbn: String?, seriesCode: String?): BooleanExpression? {
        return if (seriesIsbn != null && seriesCode != null) {
            book.series.isbn.value.eq(seriesIsbn).or(book.series.code.eq(seriesCode))
        } else if (seriesIsbn != null) {
            book.series.isbn.value.eq(seriesIsbn)
        } else if (seriesCode != null) {
            book.series.code.eq(seriesCode)
        } else {
            null
        }
    }

    private fun publisherEqual(publisherCode: String?): BooleanExpression? = publisherCode?.let { book.publisher.code.eq(publisherCode) }
}