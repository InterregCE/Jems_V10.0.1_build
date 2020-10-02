package io.cloudflight.ems.project.entity

import io.cloudflight.ems.api.project.dto.status.ProjectEligibilityAssessmentResult
import io.cloudflight.ems.user.entity.User
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.EnumType
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.OneToOne

@Entity(name = "project_eligibility_assessment")
data class ProjectEligibilityAssessment(

    @Id
    @Column(name = "project_id", nullable = false)
    val id: Long,

    @OneToOne(optional = false)
    @MapsId
    val project: Project,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val result: ProjectEligibilityAssessmentResult,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
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
