package io.cloudflight.jems.server.project.entity.partner

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.OneToOne

@Entity(name = "project_partner_contribution")
data class ProjectPartnerContribution(

    @Id
    @Column(name = "partner_id", nullable = false)
    val partnerId: Long,

    @OneToOne(optional = false)
    @MapsId
    val partner: ProjectPartner,

    @Column
    val organizationRelevance: String?,

    @Column
    val organizationRole: String?,

    @Column
    val organizationExperience: String?
) {
    override fun toString(): String {
        return "${this.javaClass.simpleName}(partnerId=$partnerId, organizationRelevance=$organizationRelevance, organizationRole=$organizationRole, organizationExperience=$organizationExperience)"
    }
}
