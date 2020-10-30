package io.cloudflight.jems.server.config

import io.cloudflight.jems.server.authentication.service.HttpSessionService
import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * this is needed to support HTML5 routing on frontend
 */
@Configuration
class WebConfiguration : WebMvcConfigurer {
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/404").setViewName("forward:/index.html")
    }

    @Bean
    fun containerCustomizer(): WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        return WebServerFactoryCustomizer { container ->
            container.addErrorPages(ErrorPage(HttpStatus.NOT_FOUND, "/404"))
        }
    }

    @Bean
    fun sessionListener(httpSessionService: HttpSessionService): ServletListenerRegistrationBean<HttpSessionService>? {
        val listenerRegBean: ServletListenerRegistrationBean<HttpSessionService> =
            ServletListenerRegistrationBean<HttpSessionService>()
        listenerRegBean.listener = httpSessionService
        return listenerRegBean
    }
}
