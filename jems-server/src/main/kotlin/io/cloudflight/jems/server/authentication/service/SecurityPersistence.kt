package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.server.authentication.model.PasswordResetToken
import java.util.UUID

interface SecurityPersistence {

    fun savePasswordResetToken(passwordResetToken: PasswordResetToken)

    fun getPasswordResetToken(token: UUID) : PasswordResetToken?

    fun deletePasswordResetToken(token: UUID)
}
