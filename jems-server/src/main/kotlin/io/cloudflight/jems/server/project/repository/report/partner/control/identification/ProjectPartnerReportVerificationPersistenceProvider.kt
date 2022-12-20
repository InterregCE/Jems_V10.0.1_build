package io.cloudflight.jems.server.project.repository.report.partner.control.identification

import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportOnTheSpotVerificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportVerificationEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.identification.toEntity
import io.cloudflight.jems.server.project.repository.report.partner.identification.toModel
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportOnTheSpotVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportVerificationPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
class ProjectPartnerReportVerificationPersistenceProvider(
    private val verificationRepository: ProjectPartnerReportVerificationRepository,
    private val onTheSpotVerificationRepository: ProjectPartnerReportOnTheSpotVerificationRepository,
    private val reportRepository: ProjectPartnerReportRepository,
) : ProjectPartnerReportVerificationPersistence {
    @Transactional(readOnly = true)
    override fun getControlReportVerification(partnerId: Long, reportId: Long): Optional<ReportVerification> {
        return verificationRepository.findByReportEntityIdAndReportEntityPartnerId(
            reportId = reportId,
            partnerId = partnerId,
        ).map { it.toModel() }
    }

    @Transactional
    override fun updateReportVerification(
        partnerId: Long,
        reportId: Long,
        reportVerification: ReportVerification
    ): ReportVerification {
        val reportEntity = reportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)

        val entity = verificationRepository.findByReportEntityIdAndReportEntityPartnerId(
            reportId = reportId,
            partnerId = partnerId,
        )

        if (entity.isEmpty) {
            val created = verificationRepository.save(reportVerification.toEntity(reportEntity))

            val verificationInstances = reportVerification.verificationInstances.map {
                    verification -> saveOnTheSpotVerification(verification, created)
            }
            return verificationRepository.save(
                created.copy(
                    generalMethodologies = reportVerification.generalMethodologies.map { it.toEntity(reportEntity.id) }.toMutableSet(),
                    verificationInstances = verificationInstances
                )
            ).toModel()
        } else {
            val verificationInstances = reportVerification.verificationInstances.map {
                    verification -> saveOnTheSpotVerification(verification, entity.get())
            }

            return verificationRepository.save(
                entity.get().copy(
                    generalMethodologies = reportVerification.generalMethodologies.map {
                        it.toEntity(reportEntity.id)
                    }.toMutableSet(),
                    verificationInstances = verificationInstances,
                    riskBasedVerificationApplied = reportVerification.riskBasedVerificationApplied,
                    riskBasedVerificationDescription = reportVerification.riskBasedVerificationDescription
                )
            ).toModel()
        }
    }

    private fun saveOnTheSpotVerification(
        verification: ReportOnTheSpotVerification,
        verificationEntity: ProjectPartnerReportVerificationEntity
    ): ProjectPartnerReportOnTheSpotVerificationEntity {
        val entity = onTheSpotVerificationRepository.findById(verification.id)
        return if (entity.isEmpty) {
            val created = onTheSpotVerificationRepository.save(verification.toEntity(verificationEntity.reportId))
            onTheSpotVerificationRepository.save(
                created.copy(
                    verificationLocations = verification.verificationLocations.map { it.toEntity(created.id) }.toMutableSet()
                )
            )
        } else {
            onTheSpotVerificationRepository.save(
                entity.get().copy(
                    verificationFrom = verification.verificationFrom,
                    verificationTo = verification.verificationTo,
                    verificationFocus = verification.verificationFocus,
                    verificationLocations = verification.verificationLocations.map { it.toEntity(entity.get().id) }.toMutableSet()
                )
            )
        }

    }
}
