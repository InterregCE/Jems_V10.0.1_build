package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.user.entity.User
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.EnumType
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "project_status")
data class ProjectStatus(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: ProjectApplicationStatus,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val user: User,

    @Column(nullable = false)
    val updated: ZonedDateTime = ZonedDateTime.now(),

    @Column
    val decisionDate: LocalDate? = null,

    @Column
    val note: String? = null

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(status=$status, user=$user, updated=$updated, note=$note)"
    }
}
