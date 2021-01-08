package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.user.entity.User
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Objects
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_status")
data class ProjectStatus(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: ProjectEntity? = null,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val status: ProjectApplicationStatus,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @field:NotNull
    val user: User,

    @field:NotNull
    val updated: ZonedDateTime = ZonedDateTime.now(),

    val decisionDate: LocalDate? = null,

    val note: String? = null

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(status=$status, user=$user, updated=$updated, note=$note)"
    }

    override fun equals(other: Any?): Boolean = (other is ProjectStatus)
        && project?.id == other.project?.id
        && status == other.status
        && updated == other.updated

    override fun hashCode(): Int = Objects.hash(project?.id, status)

}
