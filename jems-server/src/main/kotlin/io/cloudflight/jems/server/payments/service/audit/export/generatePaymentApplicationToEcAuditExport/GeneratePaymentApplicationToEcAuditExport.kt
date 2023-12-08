package io.cloudflight.jems.server.payments.service.audit.export.generatePaymentApplicationToEcAuditExport

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.PaymentApplicationToEcAuditExportPlugin
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAudit
import io.cloudflight.jems.server.payments.service.audit.export.PaymentApplicationToEcAuditExportPersistence
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcAuditExportCreated
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import java.time.ZonedDateTime
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class GeneratePaymentApplicationToEcAuditExport(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentApplicationToEcAuditExportPersistence: PaymentApplicationToEcAuditExportPersistence
) : GeneratePaymentApplicationToEcAuditExportInteractor {
    @CanUpdatePaymentsAudit
    @Transactional
    @ExceptionWrapper(GeneratePaymentApplicationToEcAuditExportException::class)
    override fun export(pluginKey: String, accountingYear: Short?, programmeFundType: ProgrammeFundTypeDTO?) {

        generatePaymentApplicationToEcAuditExport(pluginKey, accountingYear, programmeFundType).also { exportFile ->
            paymentApplicationToEcAuditExportPersistence.savePaymentApplicationToEcAuditExport(
                pluginKey,
                accountingYear,
                if (programmeFundType !== null) ProgrammeFundType.valueOf(programmeFundType.name) else null,
                ZonedDateTime.now(),
                exportFile
            )
            auditPublisher.publishEvent(
                paymentApplicationToEcAuditExportCreated(
                    context = this,
                    programmeFundType?.name,
                    accountingYear = accountingYear
                )
            )
        }
    }

    private fun generatePaymentApplicationToEcAuditExport(
        pluginKey: String,
        accountingYear: Short?,
        programmeFundType: ProgrammeFundTypeDTO?
    ): JemsFileCreate {
        return jemsPluginRegistry.get(
            PaymentApplicationToEcAuditExportPlugin::class,
            pluginKey
        ).export(
            accountingYear,
            if (programmeFundType !== null) ProgrammeFundTypeData.valueOf(programmeFundType.name) else null
        ).toJemsFile()
    }

    private fun ExportResult.toJemsFile() = JemsFileCreate(
        projectId = null,
        partnerId = null,
        name = this.fileName,
        type = JemsFileType.PaymentToEcAuditExport,
        path = JemsFileType.PaymentToEcAuditExport.generatePath(),
        size = this.content.size.toLong(),
        content = this.content.inputStream(),
        userId = 0
    )
}
