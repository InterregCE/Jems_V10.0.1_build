package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.project.dto.ProjectPartnerRole
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity(name = "project_partner")
data class ProjectPartner(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: ProjectPartnerRole,

    @Column
    val sortNumber: Int? = null,

    @OneToMany(mappedBy = "partnerContactPersonId.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val partnerContactPersons: Set<PartnerContactPerson>? = emptySet(),

    @OneToOne(mappedBy = "partner", cascade = [CascadeType.ALL])
    val partnerContribution: ProjectPartnerContribution? = null,

    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "organization_id")
    val organization: ProjectPartnerOrganization? = null

) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id, projectId=$project.id, name=$name, role=$role, sortNumber=$sortNumber, partnerContact=$partnerContactPersons, partnerContribution=$partnerContribution, organization=$organization)"
    }
}
