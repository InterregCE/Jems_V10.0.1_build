package io.cloudflight.jems.server.notification.mail.service.mail_sender_service

import ch.qos.logback.classic.Logger
import io.cloudflight.jems.server.notification.mail.config.MAIL_CONFIG_PROPERTIES_PREFIX
import io.cloudflight.jems.server.notification.mail.config.MAIL_ENABLED
import io.cloudflight.jems.server.notification.mail.config.MailConfigProperties
import io.cloudflight.jems.server.notification.mail.service.model.MailNotification
import io.cloudflight.jems.server.plugin.JemsPluginRegistryMapImpl
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import javax.mail.internet.MimeMessage

const val MAXIMUM_NUMBER_OF_MAIL_RECIPIENTS_IN_ONE_MESSAGE = 100

@Service
@ConditionalOnProperty(prefix = MAIL_CONFIG_PROPERTIES_PREFIX, name = [MAIL_ENABLED], havingValue = "true")
class DefaultMailSender(
    private val mailSender: JavaMailSender,
    private val auditPublisher: ApplicationEventPublisher,
    private val configs: MailConfigProperties
) : MailSenderService {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(JemsPluginRegistryMapImpl::class.java) as Logger
    }

    @Async
    override fun send(notification: MailNotification, afterSendHook: ((MailNotification) -> Any?)?) {
        runCatching {
            getChunkedMessagesToSend(notification).forEach {
                mailSender.send(it)
            }
            afterSendHook?.let { it(notification) }
            logger.info("mail notification with id=`${notification.id}` was sent to `${notification.recipients}}`")
            auditPublisher.publishEvent(MailSentEvent(notification))
        }.onFailure {
            logger.warn("error in sending mail notification with id=`${notification.id}` to ${notification.recipients}", it)
            auditPublisher.publishEvent(MailNotSentEvent(notification))
        }
    }

    private fun getChunkedMessagesToSend(notification: MailNotification): List<MimeMessage> =
        notification.recipients.chunked(MAXIMUM_NUMBER_OF_MAIL_RECIPIENTS_IN_ONE_MESSAGE).map { recipientsInChunk ->
            mailSender.createMimeMessage().also {
                with(MimeMessageHelper(it)) {
                    setSubject(notification.subject)
                    setFrom(configs.sender)
                    setText(notification.body, true)
                    setBcc(recipientsInChunk.toTypedArray())
                }
            }
        }
}
