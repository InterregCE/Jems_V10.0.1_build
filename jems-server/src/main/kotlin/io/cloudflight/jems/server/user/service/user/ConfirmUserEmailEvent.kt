package io.cloudflight.jems.server.user.service.user

import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable
import io.cloudflight.jems.server.notification.mail.service.model.MailNotificationInfo
import io.cloudflight.jems.server.user.service.model.User

open class ConfirmUserEmailEvent(
    open val user: User,
    override val emailTemplateFileName: String = "user-registration-confirmation.html",
) : JemsMailEvent {

    override fun getMailNotificationInfo() =
        MailNotificationInfo(
            subject = "[Jems] Please confirm your email address",
            templateVariables =
            setOf(
                Variable("name", user.name),
                Variable("surname", user.surname),
                Variable("accountValidationLink", "")
            ),
            recipients = setOf(user.email),
            messageType = "User registration confirmation"
        )
}
