package io.cloudflight.jems.server.config

import io.cloudflight.platform.spring.context.ApplicationContextProfiles
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@AutoConfiguration
@EnableSwagger2
@Profile("!${ApplicationContextProfiles.PRODUCTION} & !${ApplicationContextProfiles.TEST_CONTAINER}")
class OpenApiResolver {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())
    }

    private fun apiInfo(): ApiInfo? {
        return ApiInfoBuilder()
            .title("Jems API")
            .build()
    }

}
