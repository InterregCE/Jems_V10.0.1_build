package io.cloudflight.jems.server.notification.mail.service.mail_body_generator_service

import io.cloudflight.jems.server.common.event.JemsMailEvent
import io.cloudflight.jems.server.common.model.Variable

interface MailBodyGeneratorService {
    fun generateBodyText(event: JemsMailEvent, variables: Set<Variable>): String
}
