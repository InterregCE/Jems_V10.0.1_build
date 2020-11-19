package io.cloudflight.jems.server.project.entity.partner

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "project_partner_motivation")
data class ProjectPartnerMotivationEntity(

    @Id
    val partnerId: Long,

    @Column
    val organizationRelevance: String?,

    @Column
    val organizationRole: String?,

    @Column
    val organizationExperience: String?
) {
    fun nullIfBlank(): ProjectPartnerMotivationEntity? {
        if (organizationRelevance.isNullOrBlank()
            && organizationRole.isNullOrBlank()
            && organizationExperience.isNullOrBlank())
            return null
        return this
    }
}
