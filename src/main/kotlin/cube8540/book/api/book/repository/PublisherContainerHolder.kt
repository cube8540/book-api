package cube8540.book.api.book.repository

class PublisherContainerHolder {
    companion object {
        private var containerStrategy: PublisherContainerHolderStrategy = ThreadLocalPublisherContainerHolderStrategy()

        init {
            containerStrategy.setContainer(DefaultPublisherContainer())
        }

        fun clearContainer() {
            containerStrategy.clearContainer()
        }

        fun setContainer(container: PublisherContainer) {
            containerStrategy.setContainer(container)
        }

        fun getContainer(): PublisherContainer = containerStrategy.getContainer()
    }
}