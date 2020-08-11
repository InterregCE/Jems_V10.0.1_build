package io.cloudflight.ems.project.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.OneToOne

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
    val introProgrammeLanguage: String?

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(projectId=$projectId, title=$title, duration=$duration)"
    }
}
