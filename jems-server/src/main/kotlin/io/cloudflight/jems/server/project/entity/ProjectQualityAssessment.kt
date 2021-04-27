package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.project.dto.status.ProjectQualityAssessmentResult
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_quality_assessment")
data class ProjectQualityAssessment(

    @Id
    @Column(name = "project_id")
    @field:NotNull
    val id: Long,

    @OneToOne(optional = false)
    @MapsId
    @field:NotNull
    val project: ProjectEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val result: ProjectQualityAssessmentResult,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @field:NotNull
    val user: UserEntity,

    @field:NotNull
    val updated: ZonedDateTime = ZonedDateTime.now(),

    val note: String? = null

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(result=$result, user=$user, updated=$updated, note=$note)"
    }

    override fun equals(other: Any?): Boolean =
        this === other || other is ProjectQualityAssessment && id == other.id && project.id == other.project.id

    override fun hashCode(): Int =
        Objects.hash(id, project.id)
}
