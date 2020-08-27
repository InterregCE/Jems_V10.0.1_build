package io.cloudflight.ems.project.entity

import io.cloudflight.ems.api.project.dto.status.ProjectQualityAssessmentResult
import io.cloudflight.ems.user.entity.User
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.EnumType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.OneToOne

@Entity(name = "project_quality_assessment")
data class ProjectQualityAssessment(

    @Id
    @Column(name = "project_id", nullable = false)
    val id: Long,

    @OneToOne(optional = false)
    @MapsId
    val project: Project,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val result: ProjectQualityAssessmentResult,

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    val user: User,

    @Column(nullable = false)
    val updated: ZonedDateTime = ZonedDateTime.now(),

    @Column
    val note: String? = null

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(result=$result, user=$user, updated=$updated, note=$note)"
    }
}
