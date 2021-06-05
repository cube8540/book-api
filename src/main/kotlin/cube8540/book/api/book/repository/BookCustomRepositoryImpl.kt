package cube8540.book.api.book.repository

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.QBook
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
}