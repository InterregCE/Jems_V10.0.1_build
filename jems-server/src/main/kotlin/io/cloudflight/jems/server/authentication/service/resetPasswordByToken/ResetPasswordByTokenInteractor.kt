package io.cloudflight.jems.server.authentication.service.resetPasswordByToken

interface ResetPasswordByTokenInteractor {
    fun reset(token:String, newPassword: String)
}
