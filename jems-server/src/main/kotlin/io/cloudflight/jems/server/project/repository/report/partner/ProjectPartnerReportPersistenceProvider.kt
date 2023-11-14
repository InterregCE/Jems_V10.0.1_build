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
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.entity.report.partner.QProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.project.QProjectReportEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
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
import java.time.ZonedDateTime
import kotlin.streams.asSequence

@Repository
class ProjectPartnerReportPersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
    private val partnerRepository: ProjectPartnerRepository,
    private val jpaQueryFactory: JPAQueryFactory
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
            }.toSubmissionSummary()

    @Transactional
    override fun finalizeControlOnReportById(
        partnerId: Long,
        reportId: Long,
        controlEnd: ZonedDateTime,
    ) = partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
        .apply {
            this.status = ReportStatus.Certified
            this.controlEnd = controlEnd
        }.toSubmissionSummary()

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
        partnerReportRepository.getById(reportId).toSubmissionSummary()

    @Transactional(readOnly = true)
    override fun getProjectPartnerReportSubmissionSummary(
        partnerId: Long,
        reportId: Long
    ): ProjectPartnerReportSubmissionSummary =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).toSubmissionSummary()

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
        partnerReportRepository.findFirstByPartnerIdAndStatusOrderByIdDesc(partnerId, ReportStatus.Certified)?.id

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
        val ecExtensionPayment = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
        val ecPayment = QPaymentApplicationToEcEntity.paymentApplicationToEcEntity
        val accountingYear = QAccountingYearEntity.accountingYearEntity

        val fundsPerReportId = partnerReportCoFinancingRepository.findAllByIdReportPartnerIdIn(partnerIds)
            .groupBy { it.id.report.id }
            .mapValues { it.value.mapNotNull { it.programmeFund?.toModel() } }

        return jpaQueryFactory.select(
            reportPartner.partnerId,
            reportPartner.id,
            reportPartner.number,
            reportProject.id,
            reportProject.number,
            programmeFund,
            ecPayment.id,
            ecPayment.status,
            accountingYear,
        )
            .from(reportPartner)
            .leftJoin(reportProject).on(reportProject.id.eq(reportPartner.projectReport.id))
            .leftJoin(payment).on(payment.projectReport.id.eq(reportProject.id))
            .leftJoin(programmeFund).on(programmeFund.id.eq(payment.fund.id))
            .leftJoin(ecExtensionPayment).on(ecExtensionPayment.payment.id.eq(payment.id))
            .leftJoin(ecPayment).on(ecPayment.id.eq(ecExtensionPayment.paymentApplicationToEc.id))
            .leftJoin(accountingYear).on(accountingYear.id.eq(ecPayment.accountingYear.id))
            .where(
                reportPartner.partnerId.`in`(partnerIds)
                    .and(reportPartner.controlEnd.isNotNull)
            )
            .fetch()
            .map { it.toTmpModel { fundsPerReportId[it]!! } }
    }

    private fun Tuple.toTmpModel(fundResolver: (Long) -> List<ProgrammeFund>): CorrectionAvailableReportTmp {
        val id = get(1, Long::class.java)!!
        return CorrectionAvailableReportTmp(
            partnerId = get(0, Long::class.java)!!,
            id = id,
            reportNumber = get(2, Int::class.java)!!,
            projectReportId = get(3, Long::class.java),
            projectReportNumber = get(4, Int::class.java),
            availableReportFunds = fundResolver(id),

            paymentFund = get(5, ProgrammeFundEntity::class.java)?.toModel(),

            ecPaymentId = get(6, Long::class.java),
            ecPaymentStatus = get(7, PaymentEcStatus::class.java),
            ecPaymentAccountingYear = get(8, AccountingYearEntity::class.java)?.toModel(),
        )
    }
}
