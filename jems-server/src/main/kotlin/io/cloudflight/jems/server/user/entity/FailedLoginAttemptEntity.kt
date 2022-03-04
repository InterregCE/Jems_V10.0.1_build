package io.cloudflight.jems.server.user.entity

import java.time.Instant
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "account_failed_login_attempt")
class FailedLoginAttemptEntity(

    @EmbeddedId
    val id: FailedLoginAttemptId,

    @field:NotNull
    var count: Short,

    @field:NotNull
    var lastAttemptAt: Instant

)
