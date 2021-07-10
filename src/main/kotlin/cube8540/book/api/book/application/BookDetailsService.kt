package cube8540.book.api.book.application

import cube8540.book.api.book.domain.Isbn
import cube8540.book.api.book.domain.Series

interface BookDetailsService {
    fun getBookDetails(isbn: Isbn): BookDetails?

    fun getSeriesList(series: Series): List<BookDetails>
}