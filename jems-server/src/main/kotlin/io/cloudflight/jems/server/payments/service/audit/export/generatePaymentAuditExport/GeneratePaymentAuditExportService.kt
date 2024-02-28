package io.cloudflight.jems.server.payments.service.audit.export.generatePaymentAuditExport

import ch.qos.logback.classic.Logger
import io.cloudflight.jems.plugin.contract.export.PaymentApplicationToEcAuditExportPlugin
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.payments.service.audit.export.PaymentAuditExportPersistence
import io.cloudflight.jems.server.plugin.services.toDataModel
import io.cloudflight.jems.server.programme.service.exportProgrammeData.EXPORT_TIMEOUT_IN_MINUTES
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class GeneratePaymentAuditExportService(
    private val paymentAuditExportPersistence: PaymentAuditExportPersistence,
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GeneratePaymentAuditExportService::class.java) as Logger
    }

    @Async
    fun execute(
        plugin: PaymentApplicationToEcAuditExportPlugin,
        fund: ProgrammeFund?,
        accountingYear: AccountingYear?
    ) {
        runCatching {
            plugin.export(accountingYear?.year, fund?.type?.toDataModel()).also { result ->
                paymentAuditExportPersistence.saveExportFile(
                    plugin.getKey(),
                    fund?.type,
                    accountingYear?.year,
                    result.content,
                    true
                ).also { _ ->
                    paymentAuditExportPersistence.updateExportMetaData(
                        plugin.getKey(), fund?.id, accountingYear?.id, result.fileName, result.contentType,
                        result.startTime, endTime = ZonedDateTime.now()
                    )
                }
            }

        }.onFailure {
            logger.warn("Failed to export payment to ec audit data for '${plugin.getKey()}'", it)
            paymentAuditExportPersistence.updateExportMetaData(
                plugin.getKey(),
                fund?.id,
                accountingYear?.id,
                null,
                null,
                null,
                ZonedDateTime.now()
            )
        }.getOrNull()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveExportFileMetaData(pluginKey: String, programmeFundId: Long?, accountingYearId: Long?) {
        with(paymentAuditExportPersistence.listExportMetadata()) {
            throwIfAnyExportIsInProgress(this)
            deleteMetadataIfAlreadyExist(this, pluginKey, programmeFundId, accountingYearId)
        }
        paymentAuditExportPersistence.saveExportMetaData(
            pluginKey,
            programmeFundId,
            accountingYearId,
            ZonedDateTime.now()
        )
    }

    private fun throwIfAnyExportIsInProgress(metadataList: List<PaymentToEcExportMetadata>) {
        if (metadataList.firstOrNull {
                it.exportEndedAt == null && it.requestTime.isAfter(
                    ZonedDateTime.now().minusMinutes(EXPORT_TIMEOUT_IN_MINUTES)
                )
            } != null
        ) throw ExportInProgressException()
    }

    private fun deleteMetadataIfAlreadyExist(
        metadataList: List<PaymentToEcExportMetadata>,
        pluginKey: String,
        programmeFundId: Long?,
        accountingYearId: Long?
    ) {
        val fileToBeDeleted =
            metadataList.firstOrNull { it.pluginKey == pluginKey && it.accountingYear?.id == accountingYearId && it.fund?.id == programmeFundId }

        if (fileToBeDeleted != null)
            paymentAuditExportPersistence.deleteExportMetaData(fileToBeDeleted.id)
    }

}
