package io.cloudflight.jems.server.notification.mail.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ITemplateResolver
import org.thymeleaf.templateresolver.StringTemplateResolver

const val EMAIL_TEMPLATE_ENGINE = "emailTemplateEngine"

@Configuration
@ConditionalOnProperty(prefix = MAIL_CONFIG_PROPERTIES_PREFIX, name = [MAIL_ENABLED], havingValue = "true")
class MailSenderConfiguration : MailSenderAutoConfiguration(){

    @Bean
    @Qualifier(EMAIL_TEMPLATE_ENGINE)
    fun emilTemplateEngine(): ITemplateEngine =
        SpringTemplateEngine().also {
            it.addDialect(Java8TimeDialect())
            it.addTemplateResolver(stringTemplateResolver())
        }

    private fun stringTemplateResolver(): ITemplateResolver =
        StringTemplateResolver().also {
            it.templateMode = TemplateMode.HTML
        }
}
