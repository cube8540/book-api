package cube8540.book.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class MessageConverterTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `html syntax escape`() {
        val includingHtmlJson = "<script></script>"
        val obj = TestObject(includingHtmlJson)

        val result = objectMapper.writeValueAsString(obj)
        assertThat(result).doesNotContain("<", ">")
    }

    inner class TestObject(val data: String)
}