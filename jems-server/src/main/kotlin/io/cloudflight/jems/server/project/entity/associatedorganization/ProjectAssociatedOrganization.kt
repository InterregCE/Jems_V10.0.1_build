package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.ManyToOne

@Entity(name = "project_associated_organization")
data class ProjectAssociatedOrganization (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // consider removal of this in future (transitive dependency through partner -> project)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    val project: Project,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    val partner: ProjectPartner,

    @Column
    val nameInOriginalLanguage: String? = null,

    @Column
    val nameInEnglish: String? = null,

    @Column
    val sortNumber: Int? = null,

    @OneToMany(mappedBy = "organizationId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val addresses: MutableSet<ProjectAssociatedOrganizationAddress> = mutableSetOf(),

    @OneToMany(mappedBy = "contactId.associatedOrganizationId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val contacts: MutableSet<ProjectAssociatedOrganizationContact> = mutableSetOf(),

    @Column
    val roleDescription: String? = null

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id, projectId=${project.id}, partnerId=${partner.id}, sortNumber=$sortNumber)"
    }
}
