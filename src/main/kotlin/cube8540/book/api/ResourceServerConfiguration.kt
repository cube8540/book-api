package cube8540.book.api

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class ResourceServerConfiguration: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.requestMatchers()
                .antMatchers("/api/v1/**")
                .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/books/**")
                    .access("hasAuthority('SCOPE_management.book.register') and hasAuthority('SCOPE_management.book.modify')")
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
            .oauth2ResourceServer { it.opaqueToken() }
            .csrf().disable()
            .cors().configurationSource { CorsConfiguration().applyPermitDefaultValues() }
    }
}