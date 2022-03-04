package io.cloudflight.jems.server.user.entity

import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Embeddable
class FailedLoginAttemptId(

    @OneToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    @field:NotNull
    var user: UserEntity

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is FailedLoginAttemptId && user.email == other.user.email

    override fun hashCode(): Int = Objects.hash(user.email)

    companion object {
        private const val serialVersionUID: Long = 8479651513475586697L
    }


}
