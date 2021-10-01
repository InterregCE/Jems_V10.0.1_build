package io.cloudflight.jems.server.notification.mail.service.send_mail_on_jems_mail_event

import io.cloudflight.jems.server.common.afterCommit
import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.mail.config.MAIL_CONFIG_PROPERTIES_PREFIX
import io.cloudflight.jems.server.notification.mail.config.MAIL_ENABLED
import io.cloudflight.jems.server.notification.mail.config.MailConfigProperties
import io.cloudflight.jems.server.notification.mail.service.mail_body_generator_service.MailBodyGeneratorService
import io.cloudflight.jems.server.notification.mail.service.MailNotificationPersistence
import io.cloudflight.jems.server.notification.mail.service.mail_sender_service.MailSenderService
import io.cloudflight.jems.server.notification.mail.service.model.MailNotification
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@ConditionalOnProperty(prefix = MAIL_CONFIG_PROPERTIES_PREFIX, name = [MAIL_ENABLED], havingValue = "true")
class SendMailOnJemsMailEvent(
    private val persistence: MailNotificationPersistence,
    private val programmeDataPersistence: ProgrammeDataPersistence,
    private val mailBodyGenerator: MailBodyGeneratorService,
    private val mailService: MailSenderService,
    private val configs: MailConfigProperties
) {

    @Transactional
    @ExceptionWrapper(SendMailException::class)
    @EventListener
    fun enqueueMail(event: JemsMailEvent) {
        persistence.save(
            with(event.getMailNotificationInfo()) {
                MailNotification(
                    subject = subject,
                    body = mailBodyGenerator.generateBodyText(
                        event,
                        setOf(
                            *templateVariables.toTypedArray(),
                            *getGlobalTemplateVariables().toTypedArray()
                        )
                    ),
                    recipients = recipients.plus(configs.bccList.toTypedArray()),
                    messageType = messageType,
                )
            }
        ).afterCommit {
            mailService.send(this) { notification ->
                persistence.deleteIfExist(notification.id)
            }
        }
    }

    private fun getGlobalTemplateVariables(): List<Variable> =
        listOf(
            Variable("programmeName", programmeDataPersistence.getProgrammeName())
        )

}
