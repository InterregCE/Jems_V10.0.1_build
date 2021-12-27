package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_associated_organization")
data class ProjectAssociatedOrganization(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    var active: Boolean = true,

    // consider removal of this in future (transitive dependency through partner -> project)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @field:NotNull
    val project: ProjectEntity,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @field:NotNull
    val partner: ProjectPartnerEntity,

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

    // roleDescription
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.organizationId")
    val translatedValues: MutableSet<ProjectAssociatedOrganizationTransl> = mutableSetOf()

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id, projectId=${project.id}, partnerId=${partner.id}, sortNumber=$sortNumber)"
    }
}
