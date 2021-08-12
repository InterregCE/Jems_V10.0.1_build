package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionEntity
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
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
    var id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @field:NotNull
    var project: ProjectEntity,

    @field:NotNull
    var abbreviation: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var role: ProjectPartnerRole,

    @field:NotNull
    var sortNumber: Int = 0,

    var nameInOriginalLanguage: String? = null,

    var nameInEnglish: String? = null,

    @Enumerated(EnumType.STRING)
    var partnerType: ProjectTargetGroup? = null,

    @Enumerated(EnumType.STRING)
    var partnerSubType: PartnerSubType? = null,

    @Enumerated(EnumType.STRING)
    var nace: NaceGroupLevel? = null,

    var otherIdentifierNumber: String? = null,

    var pic: String? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "legal_status_id")
    @field:NotNull
    var legalStatus: ProgrammeLegalStatusEntity,

    var vat: String? = null,

    @Enumerated(EnumType.STRING)
    var vatRecovery: ProjectPartnerVatRecovery? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    var translatedValues: MutableSet<ProjectPartnerTranslEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "addressId.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val addresses: Set<ProjectPartnerAddressEntity>?= emptySet(),

    @OneToMany(mappedBy = "contactId.partnerId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val contacts: Set<ProjectPartnerContactEntity>? = emptySet(),

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
