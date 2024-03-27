package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "payment_partner")
class PaymentPartnerEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id")
    @field:NotNull
    val payment: PaymentEntity,

    @ManyToOne(optional = false)
    @JoinColumn(name = "partner_id")
    @field:NotNull
    val projectPartner: ProjectPartnerEntity,


    // partner when regular
    @ManyToOne(optional = true)
    @JoinColumn(name = "partner_certificate_id")
    val partnerCertificate: ProjectPartnerReportEntity?,

    // partner when FTLS
    val partnerAbbreviation: String,
    val partnerNameInOriginalLanguage: String,
    val partnerNameInEnglish: String,

    val amountApprovedPerPartner: BigDecimal?
)
