package io.cloudflight.ems.entity

import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.EnumType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "project_status")
data class ProjectStatus (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "project_id")
    val project: Project? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: ProjectApplicationStatus,

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    val user: User,

    @Column(nullable = false)
    val updated: ZonedDateTime = ZonedDateTime.now(),

    @Column
    val note: String? = null

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(status=$status, user=$user, updated=$updated, note=$note)"
    }
}
