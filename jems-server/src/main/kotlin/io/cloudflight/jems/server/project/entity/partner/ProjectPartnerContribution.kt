package io.cloudflight.jems.server.project.entity.partner

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "project_partner_contribution")
data class ProjectPartnerContribution(

    @Id
    val partnerId: Long,

    @Column
    val organizationRelevance: String?,

    @Column
    val organizationRole: String?,

    @Column
    val organizationExperience: String?
) {
    fun nullIfBlank(): ProjectPartnerContribution? {
        if (organizationRelevance.isNullOrBlank()
            && organizationRole.isNullOrBlank()
            && organizationExperience.isNullOrBlank())
            return null
        return this
    }
}
