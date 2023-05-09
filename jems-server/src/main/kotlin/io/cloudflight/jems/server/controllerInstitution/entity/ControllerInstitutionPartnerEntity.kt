package io.cloudflight.jems.server.controllerInstitution.entity

import com.querydsl.core.annotations.QueryEntity
import com.querydsl.core.annotations.QueryInit
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "controller_institution_partner_v2")
@QueryEntity
class ControllerInstitutionPartnerEntity(

    @Id
    val partnerId: Long,

    @ManyToOne(optional = true)
    var institution: ControllerInstitutionEntity?,

    // for fields that CAN'T change over time (with versioning, like projectId, call...)
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    @field:NotNull
    @QueryInit("*.*", "project.call") // to support deeper QueryDSL joins
    val partner: ProjectPartnerEntity,

    // for fields that CAN change (needs to be up-to-date)
    @field:NotNull var partnerNumber: Int,
    @field:NotNull var partnerAbbreviation: String,
    @Enumerated(EnumType.STRING)
    @field:NotNull var partnerRole: ProjectPartnerRole,
    @field:NotNull var partnerActive: Boolean,

    var addressNuts3: String?,
    @Column(name ="address_nuts3_code")
    var addressNuts3Code: String?,
    var addressCountry: String?,
    var addressCountryCode: String?,
    var addressCity: String?,
    var addressPostalCode: String?,

    @field:NotNull var projectIdentifier: String,
    @field:NotNull var projectAcronym: String,
)
