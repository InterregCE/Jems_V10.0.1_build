package io.cloudflight.jems.server.project.repository.report.partner

import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportBaseData
import io.cloudflight.jems.server.payments.accountingYears.repository.toModel
import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.QAccountingYearEntity
import io.cloudflight.jems.server.payments.entity.QPaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.QProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.entity.report.partner.QProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.QProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.project.QProjectReportEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.report.partner.identification.ProjectPartnerReportIdentificationRepository
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableReportTmp
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.ZonedDateTime
import kotlin.streams.asSequence

@Repository
class ProjectPartnerReportPersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
    private val partnerRepository: ProjectPartnerRepository,
    private val jpaQueryFactory: JPAQueryFactory,
    private val identificationRepository: ProjectPartnerReportIdentificationRepository
) : ProjectPartnerReportPersistence {

    @Transactional
    override fun updateStatusAndTimes(
        partnerId: Long,
        reportId: Long,
        status: ReportStatus,
        firstSubmissionTime: ZonedDateTime?,
        lastReSubmissionTime: ZonedDateTime?,
        lastControlReopening: ZonedDateTime?,
    ) =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
            .apply {
                this.status = status
                firstSubmission = firstSubmissionTime ?: this.firstSubmission
                lastReSubmission = lastReSubmissionTime ?: this.lastReSubmission
                this.lastControlReopening = lastControlReopening ?: this.lastControlReopening
            }.toSubmissionSummary(identificationRepository.getPartnerReportPeriod(reportId))

    @Transactional
    override fun finalizeControlOnReportById(
        partnerId: Long,
        reportId: Long,
        controlEnd: ZonedDateTime,
    ) = partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
        .apply {
            this.status = ReportStatus.Certified
            this.controlEnd = controlEnd
        }.toSubmissionSummary(identificationRepository.getPartnerReportPeriod(reportId))

    @Transactional(readOnly = true)
    override fun getPartnerReportStatusAndVersion(
        partnerId: Long,
        reportId: Long
    ): ProjectPartnerReportStatusAndVersion =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).toStatusAndVersion()

    @Transactional(readOnly = true)
    override fun getPartnerReportByProjectIdAndId(projectId: Long, reportId: Long): ProjectPartnerReportStatusAndVersion? {
        val report = partnerReportRepository.getById(reportId)
        val projectIdInEntity = partnerRepository.getProjectIdForPartner(report.partnerId)

        return if (projectId == projectIdInEntity)
            report.toStatusAndVersion()
        else
            null
    }

    @Transactional(readOnly = true)
    override fun getPartnerReportById(partnerId: Long, reportId: Long): ProjectPartnerReport =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).toModel(
            coFinancing = partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId)
        )

    @Transactional(readOnly = true)
    override fun getPartnerReportByIdUnsecured(reportId: Long): ProjectPartnerReportSubmissionSummary =
        partnerReportRepository.getById(reportId).toSubmissionSummary(identificationRepository.getPartnerReportPeriod(reportId))

    @Transactional(readOnly = true)
    override fun getProjectPartnerReportSubmissionSummary(
        partnerId: Long,
        reportId: Long
    ): ProjectPartnerReportSubmissionSummary =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).toSubmissionSummary(
            identificationRepository.getPartnerReportPeriod(reportId)
        )

    @Transactional(readOnly = true)
    override fun listPartnerReports(partnerIds: Set<Long>, statuses: Set<ReportStatus>, pageable: Pageable): Page<ProjectPartnerReportSummary> =
        partnerReportRepository.findAllByPartnerIdInAndStatusIn(partnerIds, statuses, pageable)
            .map { it.toModelSummary() }

    @Transactional(readOnly = true)
    override fun getAllPartnerReportsBaseDataByProjectId(projectId: Long): Sequence<ProjectPartnerReportBaseData> {
        return partnerReportRepository.findAllPartnerReportsBaseDataByProjectId(projectId).asSequence()
    }

    @Transactional(readOnly = true)
    override fun listCertificates(partnerIds: Set<Long>, pageable: Pageable): Page<PartnerReportCertificate> =
        partnerReportRepository.findAllCertificates(partnerIds, pageable).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getSubmittedPartnerReports(partnerId: Long): List<ProjectPartnerReportStatusAndVersion> =
        partnerReportRepository.findAllByPartnerIdAndStatusInOrderByNumberDesc(partnerId, ReportStatus.FINANCIALLY_CLOSED_STATUSES)
            .map { ProjectPartnerReportStatusAndVersion(it.id, it.status, it.applicationFormVersion) }

    @Transactional(readOnly = true)
    override fun getLastCertifiedPartnerReportId(partnerId: Long): Long? =
        partnerReportRepository.findFirstByPartnerIdAndStatusOrderByControlEndDesc(partnerId, ReportStatus.Certified)?.id

    @Transactional(readOnly = true)
    override fun getReportIdsBefore(partnerId: Long, beforeReportId: Long): Set<Long> =
        partnerReportRepository.getReportIdsForPartnerBefore(partnerId = partnerId, reportId = beforeReportId)

    @Transactional(readOnly = true)
    override fun exists(partnerId: Long, reportId: Long) =
        partnerReportRepository.existsByPartnerIdAndId(partnerId = partnerId, id = reportId)

    @Transactional(readOnly = true)
    override fun existsByStatusIn(partnerId: Long, statuses: Set<ReportStatus>) =
        partnerReportRepository.existsByPartnerIdAndStatusIn(partnerId, statuses)

    @Transactional(readOnly = true)
    override fun getCurrentLatestReportForPartner(partnerId: Long): ProjectPartnerReport? =
        partnerReportRepository.findFirstByPartnerIdOrderByIdDesc(partnerId = partnerId)?.toModel(emptyList())

    @Transactional(readOnly = true)
    override fun countForPartner(partnerId: Long): Int =
        partnerReportRepository.countAllByPartnerId(partnerId)


    @Transactional(readOnly = true)
    override fun isAnyReportCreated() =
        partnerReportRepository.count() > 0

    @Transactional
    override fun deletePartnerReportById(reportId: Long) {
        partnerReportRepository.deleteById(reportId)
    }

    @Transactional(readOnly = true)
    override fun getReportStatusById(reportId: Long): ReportStatus =
        partnerReportRepository.getById(reportId).status

    @Transactional(readOnly = true)
    override fun getAvailableReports(partnerIds: Set<Long>): List<CorrectionAvailableReportTmp> {
        val reportPartner = QProjectPartnerReportEntity.projectPartnerReportEntity
        val reportProject = QProjectReportEntity.projectReportEntity
        val payment = QPaymentEntity.paymentEntity
        val programmeFund = QProgrammeFundEntity.programmeFundEntity
        val ecPaymentExtension = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        val ecPayment = QPaymentApplicationToEcEntity.paymentApplicationToEcEntity
        val accountingYear = QAccountingYearEntity.accountingYearEntity
        val reportCoFin = QProjectPartnerReportCoFinancingEntity.projectPartnerReportCoFinancingEntity

        return jpaQueryFactory.select(
            reportPartner.partnerId,
            reportPartner.id,
            reportPartner.number,
            reportProject.id,
            reportProject.number,
            programmeFund,
            reportCoFin.total,
            ecPayment.id,
            ecPayment.status,
            accountingYear,
        )
            .from(reportPartner)
            .leftJoin(reportProject)
                .on(reportProject.eq(reportPartner.projectReport))
            .leftJoin(reportCoFin)
                .on(reportCoFin.id.report.eq(reportPartner))
            .leftJoin(programmeFund)
                .on(programmeFund.eq(reportCoFin.programmeFund))
            .leftJoin(payment)
                .on(payment.fund.eq(programmeFund).and(payment.projectReport.eq(reportProject)))
            .leftJoin(ecPaymentExtension)
                .on(ecPaymentExtension.payment.eq(payment))
            .leftJoin(ecPayment)
                .on(ecPayment.eq(ecPaymentExtension.paymentApplicationToEc))
            .leftJoin(accountingYear)
                .on(accountingYear.eq(ecPayment.accountingYear))
            .where(
                reportPartner.partnerId.`in`(partnerIds)
                    .and(reportPartner.controlEnd.isNotNull())
                    .and(reportCoFin.programmeFund.isNotNull())
            )
            .fetch()
            .map { it.toTmpModel() }
    }

    private fun Tuple.toTmpModel(): CorrectionAvailableReportTmp =
        CorrectionAvailableReportTmp(
            partnerId = get(0, Long::class.java)!!,
            id = get(1, Long::class.java)!!,
            reportNumber = get(2, Int::class.java)!!,
            projectReportId = get(3, Long::class.java),
            projectReportNumber = get(4, Int::class.java),
            availableFund = get(5, ProgrammeFundEntity::class.java)!!.toModel(),
            fundShareTotal = get(6, BigDecimal::class.java)!!,
            ecPaymentId = get(7, Long::class.java),
            ecPaymentStatus = get(8, PaymentEcStatus::class.java),
            ecPaymentAccountingYear = get(9, AccountingYearEntity::class.java)?.toModel(),
        )

}
