package io.cloudflight.jems.server.project.entity.contracting.partner

import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Column
import javax.validation.constraints.NotNull

@Entity(name = "project_contracting_partner_documents_location")
class ProjectContractingPartnerDocumentsLocationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    @field:NotNull
    val projectPartner: ProjectPartnerEntity,

    val title: String?,

    val firstName: String?,

    val lastName: String?,

    val emailAddress: String?,

    val telephoneNo: String?,

    val institutionName: String?,

    val street: String?,

    val locationNumber: String?,

    val postalCode: String?,

    val city: String?,

    val homepage: String?,

    val country: String?,

    val nutsTwoRegion: String?,

    val nutsThreeRegion: String?,

    val countryCode: String?,

    val nutsTwoRegionCode: String?,

    val nutsThreeRegionCode: String?
)
