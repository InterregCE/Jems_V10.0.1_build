package io.cloudflight.jems.server.project.entity


import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity


@Entity(name = "project_partner_organization_details")
data class ProjectPartnerOrganizationDetails(

    @EmbeddedId
    val partnerOrganizationDetailId: PartnerOrganizationDetailId,

    @Column
    val country: String?,

    @Column
    val nutsRegion2: String?,

    @Column
    val nutsRegion3: String?,

    @Column
    val street: String?,

    @Column
    val houseNumber: String?,

    @Column
    val postalCode: String?,

    @Column
    val city: String?,

    @Column
    val homepage: String?

)