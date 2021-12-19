package cube8540.book.api.book.repository

interface PublisherContainerHolderStrategy {
    fun clearContainer()

    fun setContainer(container: PublisherContainer)

    fun getContainer(): PublisherContainer
}