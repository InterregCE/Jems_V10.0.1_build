package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancing
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

@Entity(name = "project_partner")
data class ProjectPartner(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: Project,

    @Column(nullable = false)
    val abbreviation: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: ProjectPartnerRole,

    @Column
    val sortNumber: Int? = null,

    @Column
    val nameInOriginalLanguage: String? = null,

    @Column
    val nameInEnglish: String? = null,

    @Column
    val department: String? = null,

    @OneToMany(mappedBy = "addressId.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val addresses: Set<ProjectPartnerAddress>?= emptySet(),

    @OneToMany(mappedBy = "contactId.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val contacts: Set<ProjectPartnerContact>? = emptySet(),

    @OneToMany(mappedBy = "partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val partnerContribution: Set<ProjectPartnerContribution> = emptySet(),

    @OneToMany(mappedBy = "partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val financing: Set<ProjectPartnerCoFinancing> = emptySet()

    ) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id, projectId=${project.id}, abbreviation=$abbreviation, role=$role, sortNumber=$sortNumber)"
    }
}
