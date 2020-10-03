package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.FetchType

@Entity(name = "project_data")
data class ProjectData(

    @Id
    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @OneToOne(optional = false)
    @MapsId
    val project: Project,

    @Column
    val title: String?,

    @Column
    val duration: Int?,

    @Column
    val intro: String?,

    @Column
    val introProgrammeLanguage: String?,

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_policy_id")
    val priorityPolicy: ProgrammePriorityPolicy?

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(projectId=$projectId, title=$title, duration=$duration)"
    }
}
