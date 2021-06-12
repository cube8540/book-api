package cube8540.book.api.book.repository

import cube8540.book.api.book.domain.Book
import cube8540.book.api.book.domain.Isbn
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository

interface BookCustomRepository {
    fun findDetailsByIsbn(isbn: Isbn): Book?

    fun findDetailsByIsbn(isbnList: Collection<Isbn>): List<Book>

    fun findPageByCondition(condition: BookQueryCondition, pageRequest: PageRequest): Page<Book>
}

interface BookRepository: JpaRepository<Book, Isbn>, BookCustomRepository