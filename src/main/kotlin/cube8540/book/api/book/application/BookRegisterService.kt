package cube8540.book.api.book.application

interface BookRegisterService {
    fun upsertBook(upsertRequests: List<BookPostRequest>): BookPostResult
}