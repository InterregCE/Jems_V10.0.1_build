package io.cloudflight.jems.server.project.entity.contracting.partner

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "project_contracting_partner_banking_details")
class ProjectContractingPartnerBankingDetailsEntity (

    @Id
    @Column(name = "partner_id")
    @field:NotNull
    val partnerId: Long = 0,

    @Column(name = "account_holder")
    var accountHolder: String?,

    @Column(name = "account_number")
    var accountNumber: String?,

    @Column(name = "account_iban")
    var accountIBAN: String?,

    @Column(name = "account_swift_bic_code")
    var accountSwiftBICCode: String?,

    @Column(name = "bank_name")
    var bankName: String?,

    @Column(name = "street_name")
    var streetName: String?,

    @Column(name = "street_number")
    var streetNumber: String?,

    @Column(name = "postal_code")
    var postalCode: String?,

    @Column(name = "country")
    var country: String?,

    @Column(name = "nuts_two_region")
    var nutsTwoRegion: String?,

    @Column(name = "nuts_three_region")
    var nutsThreeRegion: String?
)