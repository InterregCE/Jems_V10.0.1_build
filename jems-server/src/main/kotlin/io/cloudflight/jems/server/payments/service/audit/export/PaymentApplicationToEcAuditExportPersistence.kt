package io.cloudflight.jems.server.payments.service.audit.export

import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import java.time.ZonedDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentApplicationToEcAuditExportPersistence {
    fun savePaymentApplicationToEcAuditExport(pluginKey: String, accountingYear: Short?, fundType: ProgrammeFundType?, requestTime: ZonedDateTime, file: JemsFileCreate)

    fun listPaymentApplicationToEcAuditExport(pageable: Pageable): Page<PaymentToEcExportMetadata>

    fun existsByFileId(fileId: Long): Boolean

    fun getById(fileId: Long): PaymentToEcExportMetadata
}
