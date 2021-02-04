package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionEntity
import javax.persistence.CascadeType
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
import javax.validation.constraints.NotNull

@Entity(name = "project_partner")
data class ProjectPartnerEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @field:NotNull
    val project: ProjectEntity,

    @field:NotNull
    val abbreviation: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val role: ProjectPartnerRole,

    @field:NotNull
    val sortNumber: Int = 0,

    val nameInOriginalLanguage: String? = null,

    val nameInEnglish: String? = null,

    // department
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.partnerId")
    val translatedValues: MutableSet<ProjectPartnerTranslEntity> = mutableSetOf(),

    @Enumerated(EnumType.STRING)
    val partnerType: ProjectTargetGroup? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "legal_status_id")
    @field:NotNull
    val legalStatus: ProgrammeLegalStatus,

    val vat: String? = null,

    val vatRecovery: Boolean? = null,

    @OneToMany(mappedBy = "addressId.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val addresses: Set<ProjectPartnerAddress>?= emptySet(),

    @OneToMany(mappedBy = "contactId.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val contacts: Set<ProjectPartnerContact>? = emptySet(),

    @OneToMany(mappedBy = "partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val motivation: Set<ProjectPartnerMotivationEntity> = emptySet(),

    @OneToMany(mappedBy = "coFinancingFundId.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val financing: Set<ProjectPartnerCoFinancingEntity> = emptySet(),

    @OneToMany(mappedBy = "partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val partnerContributions: List<ProjectPartnerContributionEntity> = emptyList()

    ) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id, projectId=${project.id}, abbreviation=$abbreviation, role=$role, sortNumber=$sortNumber)"
    }
}
