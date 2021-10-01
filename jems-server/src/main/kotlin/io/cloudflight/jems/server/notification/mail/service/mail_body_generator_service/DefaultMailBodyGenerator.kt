package io.cloudflight.jems.server.notification.mail.service.mail_body_generator_service

import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.mail.config.EMAIL_TEMPLATE_ENGINE
import io.cloudflight.jems.server.notification.mail.config.MAIL_CONFIG_PROPERTIES_PREFIX
import io.cloudflight.jems.server.notification.mail.config.MAIL_ENABLED
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.Context

const val EMAIL_TEMPLATES_BUCKET_NAME = "email-templates"

@Service
@ConditionalOnProperty(prefix = MAIL_CONFIG_PROPERTIES_PREFIX, name = [MAIL_ENABLED], havingValue = "true")
class DefaultMailBodyGenerator(
    private val minioStorage: MinioStorage,
    @Qualifier(EMAIL_TEMPLATE_ENGINE)
    private val templateEngine: ITemplateEngine
) : MailBodyGeneratorService {

    override fun generateBodyText(event: JemsMailEvent, variables: Set<Variable>): String =
        with(Context()) {
            setVariables(variables.toMap())
            templateEngine.process(getEmailBodyTemplate(event), this)
        }

    private fun getEmailBodyTemplate(event: JemsMailEvent): String =
        String(minioStorage.getFile(EMAIL_TEMPLATES_BUCKET_NAME, event.emailTemplateFileName), Charsets.UTF_8)

    fun Set<Variable>.toMap() =
        mutableMapOf<String, Any?>().also { map ->
            map.putAll(
                this.map { it.name to it.value }
            )
        }
}
