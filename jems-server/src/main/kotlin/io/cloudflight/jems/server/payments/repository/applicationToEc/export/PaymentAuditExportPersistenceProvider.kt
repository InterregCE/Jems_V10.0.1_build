package io.cloudflight.jems.server.payments.repository.applicationToEc.export

import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.payments.accountingYears.repository.AccountingYearRepository
import io.cloudflight.jems.server.payments.entity.PaymentAuditExportEntity
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.payments.service.audit.export.PaymentAuditExportPersistence
import io.cloudflight.jems.server.payments.service.audit.export.generatePaymentAuditExport.PaymentAuditExportMetaDataNotFoundException
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class PaymentAuditExportPersistenceProvider(
    private val paymentAuditExportRepository: PaymentAuditExportRepository,
    private val minioStorage: MinioStorage,
    private val fundRepository: ProgrammeFundRepository,
    private val accountingYearRepository: AccountingYearRepository
) : PaymentAuditExportPersistence {

    companion object {
        const val EC_PAYMENT_AUDIT_EXPORT_BUCKET = "ec-payment-audit-export"
    }

    @Transactional(readOnly = true)
    override fun listPaymentApplicationToEcAuditExport(pageable: Pageable): Page<PaymentToEcExportMetadata> =
        paymentAuditExportRepository.findAllByOrderByRequestTimeDesc(pageable).toModel()


    @Transactional(readOnly = true)
    override fun getById(fileId: Long): PaymentToEcExportMetadata =
        paymentAuditExportRepository.findById(fileId).get()
            .toModel()

    override fun getExportFile(pluginKey: String, fundType: ProgrammeFundType?, accountingYear: Short?): ByteArray =
        minioStorage.getFile(EC_PAYMENT_AUDIT_EXPORT_BUCKET, getFilePath(pluginKey, fundType, accountingYear))

    @Transactional
    override fun updateExportMetaData(
        pluginKey: String,
        fundId: Long?,
        accountingYearId: Long?,
        fileName: String?,
        contentType: String?,
        startTime: ZonedDateTime?,
        endTime: ZonedDateTime
    ) =
        getPaymentAuditExportMetadataOrThrow(pluginKey, fundId, accountingYearId).apply {
            this.fileName = fileName
            this.contentType = contentType
            this.exportStartedAt = startTime
            this.exportEndedAt = endTime
        }.toModel()

    private fun getPaymentAuditExportMetadataOrThrow(
        pluginKey: String,
        fundId: Long?,
        accountingYearId: Long?
    ): PaymentAuditExportEntity =
        paymentAuditExportRepository.findByPluginKeyAndProgrammeFundIdAndAccountingYearId(
            pluginKey,
            fundId,
            accountingYearId
        ).orElseThrow {
            PaymentAuditExportMetaDataNotFoundException()
        }

    override fun saveExportFile(
        pluginKey: String,
        fundType: ProgrammeFundType?,
        accountingYear: Short?,
        content: ByteArray,
        overwriteIfExists: Boolean
    ) {
        minioStorage.saveFile(
            EC_PAYMENT_AUDIT_EXPORT_BUCKET,
            getFilePath(pluginKey, fundType, accountingYear),
            content.size.toLong(),
            content.inputStream(),
            overwriteIfExists
        )
    }

    private fun getFilePath(pluginKey: String, fundType: ProgrammeFundType?, accountingYear: Short?) =
        pluginKey + fundType.toString() + accountingYear.toString()


    @Transactional
    override fun saveExportMetaData(
        pluginKey: String, programmeFundId: Long?, accountingYearId: Long?, requestTime: ZonedDateTime
    ): PaymentToEcExportMetadata {
        val programmeFundEntity = programmeFundId?.let { fundRepository.getReferenceById(programmeFundId) }
        val accountingYearEntity = accountingYearId?.let { accountingYearRepository.getReferenceById(accountingYearId) }

        return paymentAuditExportRepository.save(
            PaymentAuditExportEntity(
                pluginKey = pluginKey,
                programmeFund = programmeFundEntity,
                accountingYear = accountingYearEntity,
                requestTime = requestTime
            )
        ).toModel()
    }

    @Transactional
    override fun deleteExportMetaData(id: Long) =
        paymentAuditExportRepository.deleteById(id)

    @Transactional(readOnly = true)
    override fun listExportMetadata(): List<PaymentToEcExportMetadata> =
        paymentAuditExportRepository.findAllByOrderByRequestTimeDesc().toModel()

}
