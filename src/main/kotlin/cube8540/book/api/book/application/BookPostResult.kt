package cube8540.book.api.book.application

data class BookPostResult(
    var successBooks: List<String>,
    var failedBooks: List<BookPostErrorReason>
)