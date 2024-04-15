package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.temporaryModels.AuditControlCorrectionBulkTmpObject
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.stream.Stream

@Repository
interface AuditControlCorrectionRepository : JpaRepository<AuditControlCorrectionEntity, Long> {

    fun findFirstByAuditControlIdOrderByOrderNrDesc(auditControlId: Long): AuditControlCorrectionEntity?

    fun getAllByAuditControlAndStatusAndOrderNrBefore(
        auditControl: AuditControlEntity,
        status: AuditControlStatus,
        orderNr: Int
    ): List<AuditControlCorrectionEntity>

    fun getAllByAuditControlIdAndStatus(auditControlId: Long, status: AuditControlStatus): List<AuditControlCorrectionEntity>

    fun existsByProcurementId(procurementId: Long): Boolean

    @Query("""
        SELECT new io.cloudflight.jems.server.project.entity.auditAndControl.temporaryModels.AuditControlCorrectionBulkTmpObject(
            ac.project.id,
            p.customIdentifier,
            p.acronym,
            pprio.objective,
            pprio.code,
            ppso.programmeObjectivePolicy,
            ppso.code,
            pcm.typologyProv94,
            pcm.typologyProv95,
            ac.number,
            ac.status,
            ac.controllingBody,
            ac.controlType,
            ac.startDate,
            ac.endDate,
            ac.finalReportDate,
            ac.totalControlledAmount,
            ac.comment,
            acc.orderNr,
            acc.status,
            acc.correctionType,
            accfu.orderNr,
            acc.followUpOfCorrectionType,
            acc.repaymentDate,
            acc.lateRepayment,
            pp.role,
            pp.sortNumber,
            rpp.identification.partnerAbbreviation,
            pp.abbreviation,
            rp.number,
            rpp.number,
            fund.id,
            fund.type,
            rppe.costCategory,
            acc.costCategory,
            rppp.contractName,
            accf.deduction,
            accf.fundAmount,
            accf.publicContribution,
            accf.autoPublicContribution,
            accf.privateContribution,
            accf.infoSentBeneficiaryDate,
            accf.infoSentBeneficiaryComment,
            accf.correctionType,
            accf.clericalTechnicalMistake,
            accf.goldPlating,
            accf.suspectedFraud,
            accf.correctionComment,
            acc.impact,
            acc.impactComment,
            acc.projectModificationId,
            pate.id,
            pate.accountingYear.id,
            pateYear.startDate,
            pateYear.endDate,
            accm.scenario,
            accm.comment,
            ptece.paymentApplicationToEc.id,
            pateIncluded.accountingYear.id,
            pateIncludedYear.startDate,
            pateIncludedYear.endDate,
            paIncluded.accountingYear.id,
            paIncludedYear.startDate,
            paIncludedYear.endDate,
            COALESCE(
                ptece.fundAmount + ptece.publicContribution + ptece.autoPublicContribution + ptece.privateContribution,
                pace.fundAmount + pace.publicContribution + pace.autoPublicContribution + pace.privateContribution
            ),
            ptece.correctedTotalEligibleWithoutArt94or95,
            ptece.correctedUnionContribution,
            COALESCE(ptece.fundAmount, pace.fundAmount),
            COALESCE(
                ptece.publicContribution + ptece.autoPublicContribution + ptece.privateContribution,
                pace.publicContribution + pace.autoPublicContribution + pace.privateContribution
            ),
            COALESCE(ptece.correctedPublicContribution, pace.correctedPublicContribution),
            COALESCE(ptece.correctedAutoPublicContribution, pace.correctedAutoPublicContribution),
            COALESCE(ptece.correctedPrivateContribution, pace.correctedPrivateContribution),
            COALESCE(ptece.comment, pace.comment)
        )
        FROM #{#entityName} acc
            LEFT JOIN audit_control ac ON acc.auditControl.id = ac.id
            LEFT JOIN project p ON ac.project.id = p.id
            LEFT JOIN programme_priority_specific_objective ppso ON p.priorityPolicy = ppso.programmeObjectivePolicy
            LEFT JOIN programme_priority pprio ON ppso.programmePriority.id = pprio.id
            LEFT JOIN project_contracting_monitoring pcm ON p.id = pcm.projectId
            LEFT JOIN #{#entityName} accfu ON acc.followUpOfCorrection.id = accfu.id
            LEFT JOIN report_project_partner rpp ON acc.partnerReport.id = rpp.id
            LEFT JOIN report_project rp ON rpp.projectReport.id = rp.id
            LEFT JOIN project_partner pp ON acc.lumpSumPartnerId = pp.id OR rpp.partnerId = pp.id
            LEFT JOIN programme_fund fund ON acc.programmeFund.id = fund.id
            LEFT JOIN report_project_partner_expenditure rppe ON acc.expenditure.id = rppe.id
            LEFT JOIN report_project_partner_procurement rppp ON acc.procurementId = rppp.id
            LEFT JOIN #{#entityName}_finance accf ON acc.id = accf.correctionId
            LEFT JOIN payment pay ON (fund.id = pay.fund.id
                                         AND (rp.id = pay.projectReport.id OR acc.lumpSum = pay.projectLumpSum))
            LEFT JOIN payment_to_ec_extension ptee ON pay.id = ptee.payment.id
            LEFT JOIN payment_applications_to_ec pate ON ptee.paymentApplicationToEc.id = pate.id
            LEFT JOIN accounting_years pateYear ON pate.accountingYear.id = pateYear.id
            LEFT JOIN #{#entityName}_measure accm ON acc.id = accm.correctionId

            LEFT JOIN payment_to_ec_correction_extension ptece ON acc.id = ptece.correctionId
            LEFT JOIN payment_applications_to_ec pateIncluded ON ptece.paymentApplicationToEc.id = pateIncluded.id
            LEFT JOIN accounting_years pateIncludedYear ON pateIncluded.accountingYear.id = pateIncludedYear.id
            LEFT JOIN payment_account_correction_extension pace ON acc.id = pace.correctionId
            LEFT JOIN payment_account paIncluded ON pace.paymentAccount.id = paIncluded.id
            LEFT JOIN accounting_years paIncludedYear ON paIncluded.accountingYear.id = paIncludedYear.id
        WHERE acc.programmeFund.id = :fundId
    """)
    fun findAllCorrectionsForExport(fundId: Long): Stream<AuditControlCorrectionBulkTmpObject>

}
