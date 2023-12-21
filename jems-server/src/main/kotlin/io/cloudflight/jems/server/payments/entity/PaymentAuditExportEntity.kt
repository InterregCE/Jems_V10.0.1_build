package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "payment_audit_export_metadata")
class PaymentAuditExportEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val pluginKey: String,

    @ManyToOne
    @JoinColumn(name = "accounting_year_id")
    val accountingYear: AccountingYearEntity? = null,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    val programmeFund: ProgrammeFundEntity? = null,

    var fileName: String? = null,

    var contentType: String? = null,

    @field:NotNull
    var requestTime: ZonedDateTime,

    var exportStartedAt: ZonedDateTime? = null,

    var exportEndedAt: ZonedDateTime? = null,
)
