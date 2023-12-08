package io.cloudflight.jems.server.payments.repository.applicationToEc.export

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcAuditExportEntity
import io.cloudflight.jems.server.payments.model.ec.export.PaymentApplicationToEcFull
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.payments.service.audit.export.PaymentApplicationToEcAuditExportPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import java.time.ZonedDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PaymentApplicationToEcAuditExportPersistenceProvider(
    private val paymentApplicationToEcAuditExportRepository: PaymentApplicationToEcAuditExportRepository,
    private val fileRepository: JemsProjectFileService,
) : PaymentApplicationToEcAuditExportPersistence {

    @Transactional(readOnly = true)
    override fun listPaymentApplicationToEcAuditExport(pageable: Pageable): Page<PaymentToEcExportMetadata> {
        return paymentApplicationToEcAuditExportRepository.findAll(pageable).toModel()
    }

    @Transactional(readOnly = true)
    override fun existsByFileId(fileId: Long): Boolean {
        return paymentApplicationToEcAuditExportRepository.existsById(fileId)
    }

    @Transactional(readOnly = true)
    override fun getById(fileId: Long): PaymentToEcExportMetadata {
        return paymentApplicationToEcAuditExportRepository.findById(fileId).get().toModel()
    }

    @Transactional
    override fun savePaymentApplicationToEcAuditExport(pluginKey: String, accountingYear: Short?, fundType: ProgrammeFundType?, requestTime: ZonedDateTime, file: JemsFileCreate) {
        persistFileAndCreateLink(file = file) {
            paymentApplicationToEcAuditExportRepository.save(createPaymentApplicationToEcAuditExportEntity(pluginKey, accountingYear, fundType, requestTime, it))
        }
    }

    private fun persistFileAndCreateLink(file: JemsFileCreate, additionalStep: (JemsFileMetadataEntity) -> Unit) =
        fileRepository.persistFileAndPerformAction(file = file, additionalStep = additionalStep)

    private fun createPaymentApplicationToEcAuditExportEntity(pluginKey: String, accountingYear: Short?, fundType: ProgrammeFundType?, requestTime: ZonedDateTime, file: JemsFileMetadataEntity): PaymentApplicationToEcAuditExportEntity {
        return PaymentApplicationToEcAuditExportEntity(
            pluginKey = pluginKey,
            generatedFile = file,
            accountingYear = accountingYear,
            fundType = fundType,
            requestTime = requestTime
        )
    }
}
