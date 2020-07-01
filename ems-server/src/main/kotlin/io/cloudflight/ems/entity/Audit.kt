package io.cloudflight.ems.entity

import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.security.model.CurrentUser
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Document(indexName = "audit-log", type = "audit")
data class Audit(
    @Id
    @Field(type = FieldType.Text)
    val id: String?,

    @Field(type = FieldType.Date, store = true, format = DateFormat.date_time)
    val timestamp: String?,

    @Field(type = FieldType.Keyword, store = true)
    val action: AuditAction?,

    @Field(type = FieldType.Keyword, store = true)
    val projectId: String?,

    @Field(type = FieldType.Keyword, store = true)
    val username: String?,

    @Field(type = FieldType.Text, store = true, index = false)
    val description: String?
) {

    companion object {
        fun projectSubmitted(currentUser: CurrentUser?, projectId: String): Audit {
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.PROJECT_SUBMISSION,
                projectId = projectId,
                username = currentUser?.user?.email,
                description = "submission of the project application to the system"
            )
        }

        fun projectFileDeleted(currentUser: CurrentUser?, projectId: Long, file: ProjectFile): Audit {
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.PROJECT_FILE_DELETE,
                projectId = projectId.toString(),
                username = currentUser?.user?.email,
                description = "document ${file.name} deleted from application $projectId"
            )
        }

        fun userLoggedIn(email: String): Audit {
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.USER_LOGGED_IN,
                projectId = null,
                username = email,
                description = "user with email ${email} logged in"
            )
        }

        fun userLoggedOut(email: String): Audit {
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.USER_LOGGED_OUT,
                projectId = null,
                username = email,
                description = "user with email ${email} logged out"
            )
        }

        fun userCreated(currentUser: CurrentUser?, createdUser: OutputUser): Audit {
            val author = currentUser?.user?.email
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.USER_CREATED,
                projectId = null,
                username = author,
                description = "new user ${createdUser.email} with role ${createdUser.userRole.name} has been created by $author"
            )
        }

        fun userRoleChanged(currentUser: CurrentUser?, user: OutputUser): Audit {
            val author = currentUser?.user?.email
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.USER_ROLE_CHANGED,
                projectId = null,
                username = author,
                description = "user role '${user.userRole.name}' has been assigned to ${user.email} by $author"
            )
        }

        fun userDataChanged(currentUser: CurrentUser?, oldUser: OutputUser, newUser: OutputUser): Audit {
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.USER_DATA_CHANGED,
                projectId = null,
                username = currentUser?.user?.email,
                description = "user data changed from '${oldUser.email}, ${oldUser.name}, ${oldUser.surname}'" +
                    " to '${newUser.email}, ${newUser.name}, ${newUser.surname}'"
            )
        }

        fun applicantRegistered(createdUser: OutputUser): Audit {
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.USER_REGISTERED,
                projectId = null,
                username = createdUser.email,
                description = "new user '${createdUser.name} ${createdUser.surname}' with role '${createdUser.userRole.name}' registered"
            )
        }

        fun userSessionExpired(email: String): Audit {
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.USER_SESSION_EXPIRED,
                projectId = null,
                username = email,
                description = "user with email ${email} was logged out by the system"
            )
        }

        fun passwordChanged(initiator: CurrentUser?, changedUser: OutputUser): Audit {
            return Audit(
                id = null,
                timestamp = getElasticTimeNow(),
                action = AuditAction.PASSWORD_CHANGED,
                projectId = null,
                username = initiator?.user?.email,
                description = if (initiator?.user?.id == changedUser.id)
                    "Password of user '${changedUser.name} ${changedUser.surname}' (${changedUser.email}) has been changed by himself/herself"
                else "Password of user '${changedUser.name} ${changedUser.surname}' (${changedUser.email}) has been changed by user ${initiator?.user?.email}"
            )
        }

        private fun getElasticTimeNow(): String {
            return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)
        }

    }

    // default constructor is needed for deserialization
    constructor() : this(null, null, null, null, null, null)

}
