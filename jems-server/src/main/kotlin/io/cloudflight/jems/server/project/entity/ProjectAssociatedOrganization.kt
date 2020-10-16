package io.cloudflight.jems.server.project.entity

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
import javax.persistence.OneToOne

@Entity(name = "project_associated_organization")
data class ProjectAssociatedOrganization (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    val nameInOriginalLanguage: String? = null,

    @Column
    val nameInEnglish: String? = null,

    @OneToOne(mappedBy = "organization", cascade = [CascadeType.ALL])
    val organizationAddress: ProjectAssociatedOrganizationDetail? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project,

    @Column
    val sortNumber: Int? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    val partner: ProjectPartner,

    @OneToMany(mappedBy = "associatedOrganizationContactId.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val associatedOrganizationContacts: Set<AssociatedOrganizationContact>? = null
) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id, projectId=$project.id, sortNumber=$sortNumber, partnerContact=$associatedOrganizationContacts, organization=$organizationAddress)"
    }
}
