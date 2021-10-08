package io.cloudflight.jems.server.mail.confirmation.service

import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.mail.confirmation.service.model.MailConfirmation
import io.cloudflight.jems.server.user.service.model.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.UUID

// todo should be removed
@Service
class MailConfirmationService(private val confirmationMailPersistence: MailConfirmationPersistence,
                              private val eventPublisher: ApplicationEventPublisher,
                              private val configs: AppProperties) {


    fun createConfirmationEmail(user: User) {
        val confirmationMail = confirmationMailPersistence.save(MailConfirmation(token = UUID.randomUUID(), userId = user.id, timestamp = ZonedDateTime.now()))
        val confirmationLink = "${configs.serverUrl}/registrationConfirmation?token=${confirmationMail.token}"
        eventPublisher.publishEvent(ConfirmUserEmailEvent(user, confirmationLink))
    }
}
