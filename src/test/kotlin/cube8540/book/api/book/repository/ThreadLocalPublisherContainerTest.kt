package cube8540.book.api.book.repository

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ThreadLocalPublisherContainerTest {

    private val strategy = ThreadLocalPublisherContainerHolderStrategy()

    @Test
    fun `get container if strategy has not container`() {
        strategy.clearContainer()

        val getContainer = strategy.getContainer()
        assertThat(getContainer).isInstanceOf(DefaultPublisherContainer::class.java)

        val getAgain = strategy.getContainer()
        assertThat(getAgain).isEqualTo(getContainer)
    }

    @Test
    fun `get container if strategy has container`() {
        val container: PublisherContainer = mockk(relaxed = true)

        strategy.setContainer(container)

        val getContainer = strategy.getContainer()
        assertThat(getContainer).isEqualTo(container)
    }
}