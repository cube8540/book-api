package cube8540.book.api.book.repository

internal class ThreadLocalPublisherContainerHolderStrategy: PublisherContainerHolderStrategy {

    companion object {
        private val threadLocalContainer: ThreadLocal<PublisherContainer> = ThreadLocal()
    }

    override fun clearContainer() {
        threadLocalContainer.remove()
    }

    override fun setContainer(container: PublisherContainer) {
        threadLocalContainer.set(container)
    }

    override fun getContainer(): PublisherContainer {
        var container = threadLocalContainer.get()
        if (container == null) {
            container = DefaultPublisherContainer()
            threadLocalContainer.set(container)
        }
        return container
    }
}