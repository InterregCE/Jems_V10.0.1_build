package io.cloudflight.jems.server.payments.service.audit.export

import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

interface PaymentApplicationToEcAuditExportPersistence {

    fun saveExportFile(
        pluginKey: String,
        fundType: ProgrammeFundType?,
        accountingYear: Short?,
        content: ByteArray,
        overwriteIfExists: Boolean = true
    )

    fun saveExportMetaData(
        pluginKey: String, programmeFundId: Long?,
        accountingYearId: Long?, requestTime: ZonedDateTime
    ): PaymentToEcExportMetadata

    fun listPaymentApplicationToEcAuditExport(pageable: Pageable): Page<PaymentToEcExportMetadata>

    fun updateExportMetaData(
        pluginKey: String,
        fundId: Long?,
        accountingYearId: Long?,
        fileName: String?,
        contentType: String?,
        startTime: ZonedDateTime?,
        endTime: ZonedDateTime
    ): PaymentToEcExportMetadata

    fun getById(fileId: Long): PaymentToEcExportMetadata

    fun getExportFile(pluginKey: String, fundType: ProgrammeFundType?, accountingYear: Short?): ByteArray

    fun deleteExportMetaData(id: Long)

    fun listExportMetadata(): List<PaymentToEcExportMetadata>
}
