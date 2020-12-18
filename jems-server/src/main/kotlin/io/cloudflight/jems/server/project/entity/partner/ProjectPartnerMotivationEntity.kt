package io.cloudflight.jems.server.project.entity.partner

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity(name = "project_partner_motivation")
data class ProjectPartnerMotivationEntity(

    @Id
    val partnerId: Long,

    // organizationRelevance, organizationRole, organizationExperience
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.partnerId")
    val translatedValues: Set<ProjectPartnerMotivationTranslEntity> = emptySet()

) {
    fun nullIfBlank(): ProjectPartnerMotivationEntity? {
        if (translatedValues.isNullOrEmpty())
            return null
        return this
    }
}
