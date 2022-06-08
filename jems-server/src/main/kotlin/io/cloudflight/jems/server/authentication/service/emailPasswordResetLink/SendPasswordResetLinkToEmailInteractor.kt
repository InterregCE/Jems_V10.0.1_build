package io.cloudflight.jems.server.authentication.service.emailPasswordResetLink

interface SendPasswordResetLinkToEmailInteractor {
    fun send(email: String)
}
