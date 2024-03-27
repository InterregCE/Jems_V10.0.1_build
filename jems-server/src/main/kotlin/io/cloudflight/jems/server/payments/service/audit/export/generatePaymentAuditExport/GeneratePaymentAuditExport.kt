package io.cloudflight.jems.server.payments.service.audit.export.generatePaymentAuditExport

import io.cloudflight.jems.plugin.contract.export.PaymentApplicationToEcAuditExportPlugin
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.accountingYears.service.AccountingYearPersistence
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAudit
import io.cloudflight.jems.server.payments.service.paymentApplicationToEcAuditExportCreated
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class GeneratePaymentAuditExport(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val exportEcPaymentAuditService: GeneratePaymentAuditExportService,
    private val programmeFundPersistence: ProgrammeFundPersistence,
    private val accountingYearPersistence: AccountingYearPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : GeneratePaymentAuditExportInteractor {

    @CanRetrievePaymentsAudit
    @Transactional
    @ExceptionWrapper(GeneratePaymentAuditExportException::class)
    override fun export(pluginKey: String, programmeFundId: Long?, accountingYearId: Long?) {
        val programmeFund = programmeFundId?.let { programmeFundPersistence.getById(programmeFundId) }
        val accountingYear =  accountingYearId?.let { accountingYearPersistence.getById(accountingYearId) }

        jemsPluginRegistry.get(PaymentApplicationToEcAuditExportPlugin::class, pluginKey).also {
            synchronized(this) {
                exportEcPaymentAuditService.saveExportFileMetaData(it.getKey(), programmeFundId, accountingYearId)
            }
        }.also {
            exportEcPaymentAuditService.execute(it, programmeFund, accountingYear)
            auditPublisher.publishEvent(
                paymentApplicationToEcAuditExportCreated(
                    context = this,
                    programmeFundType = programmeFund?.type?.name,
                    accountingYear = accountingYear?.year
                )
            )
        }
    }
}
