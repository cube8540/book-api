package cube8540.book.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.Filter

@Configuration
class WebMvcConfiguration: WebMvcConfigurer {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.removeIf { it is MappingJackson2HttpMessageConverter }
        converters.add(MappingJackson2HttpMessageConverter(escapeObjectMapper()))
    }

    @Bean
    @Primary
    fun escapeObjectMapper(): ObjectMapper {
        val timeModule = JavaTimeModule()
            .addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE))
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE))

        val mapper = ObjectMapper()
            .registerModule(timeModule)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)

        mapper.factory.characterEscapes = HtmlCharacterEscapes()
        return mapper
    }

    @Bean
    fun xssEscapeServletFilter(): FilterRegistrationBean<out Filter> {
        val filterBean = FilterRegistrationBean(XssEscapeServletFilter())

        filterBean.urlPatterns = Collections.singleton("/*")
        return filterBean
    }

}