package cube8540.book.api

import com.fasterxml.jackson.databind.ObjectMapper
import cube8540.book.api.book.endpoint.BookAPIEndpointV1
import cube8540.book.api.book.endpoint.BookRegisterRequestV1
import javax.servlet.Filter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@SpringBootTest
@ActiveProfiles("test")
class ResourceServerConfigurationTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Mock
    private lateinit var endpoint: BookAPIEndpointV1

    @Autowired
    private lateinit var springSecurityFilterChain: Filter

    private lateinit var mvc: MockMvc

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        this.mvc = MockMvcBuilders
            .standaloneSetup(endpoint)
            .apply<StandaloneMockMvcBuilder?>(springSecurity(springSecurityFilterChain))
            .build()
    }

    @Test
    fun `access book lookup api v1 without scope`() {
        mvc.perform(get("/api/v1/books").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    @Test
    fun `access book register api v1 without scope`() {
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_api.book.write"])
    fun `access book register api v1 with book register and modify scope`() {
        val request = BookRegisterRequestV1(emptyList())

        mvc.perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `access book details api v1 without scope`() {
        mvc.perform(get("/api/v1/books/1234").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }
}