package io.cloudflight.jems.server.payments.entity

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "payment_application_to_ec_audit_export")
class PaymentApplicationToEcAuditExportEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val pluginKey: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "generated_file_id")
    @field:NotNull
    val generatedFile: JemsFileMetadataEntity,

    val accountingYear: Short?,

    val fundType: ProgrammeFundType?,

    @field:NotNull
    var requestTime: ZonedDateTime,

    var exportStartedAt: ZonedDateTime? = null,

    var exportEndedAt: ZonedDateTime? = null,
)
