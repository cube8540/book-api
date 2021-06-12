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
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.format.datetime.DateFormatter
import org.springframework.format.datetime.DateFormatterRegistrar
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.format.support.DefaultFormattingConversionService
import org.springframework.format.support.FormattingConversionService
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.Filter

@Configuration
class WebMvcConfiguration: WebMvcConfigurationSupport() {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.removeIf { it is MappingJackson2HttpMessageConverter }
        converters.add(MappingJackson2HttpMessageConverter(escapeObjectMapper()))
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        val pageableHandlerMethodArgumentResolver = PageableHandlerMethodArgumentResolver()
        pageableHandlerMethodArgumentResolver.setOneIndexedParameters(true)
        pageableHandlerMethodArgumentResolver.setMaxPageSize(100)
        pageableHandlerMethodArgumentResolver.setFallbackPageable(PageRequest.of(0, 10))

        argumentResolvers.add(pageableHandlerMethodArgumentResolver)
    }

    @Bean
    @Primary
    override fun mvcConversionService(): FormattingConversionService {
        val conversionService = DefaultFormattingConversionService(false)

        val dateTimeRegistrar = DateTimeFormatterRegistrar()
        dateTimeRegistrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyyMMdd"))
        dateTimeRegistrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        dateTimeRegistrar.registerFormatters(conversionService)

        val dateRegistrar = DateFormatterRegistrar()
        dateRegistrar.setFormatter(DateFormatter("yyyyMMdd"))
        dateRegistrar.registerFormatters(conversionService)

        return conversionService
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