package io.cloudflight.ems.project.entity

import io.cloudflight.ems.api.project.dto.ProjectPartnerRole
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "project_partner")
data class ProjectPartner(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    val project: Project,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: ProjectPartnerRole,

    @Column
    val sortNumber: Int? = null

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id, projectId=$project.id, name=$name, role=$role, sortNumber=$sortNumber)"
    }
}
