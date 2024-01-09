package io.cloudflight.jems.server.payments.repository.regular

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis.FallsUnderArticle94Or95
import io.cloudflight.jems.server.payments.model.regular.PaymentToEcExtensionTmp
import io.cloudflight.jems.server.payments.model.regular.PaymentToProjectTmp
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.project.entity.contracting.QProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.entity.lumpsum.QProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.project.QProjectReportEntity
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption.No
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun Sort.toQueryDslOrderBy(): OrderSpecifier<*> {
    val orderBy = if (isSorted) this.get().findFirst().get() else Sort.Order.desc("id")

    val specPayment = QPaymentEntity.paymentEntity
    val specPaymentToEcExtensionEntity = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity
    val dfg = when (orderBy.property) {
        "type" -> specPayment.type
        "projectCustomIdentifier" -> specPayment.projectCustomIdentifier
        "projectAcronym" -> specPayment.projectAcronym
        "project.contractedDecision.updated" -> specPayment.project.contractedDecision.updated
        "fund.type" -> specPayment.fund.type
        "id" -> specPayment.id
        "projectReport.number" -> specPayment.projectReport?.number
        "projectReport.verificationEndDate" -> CaseBuilder().`when`(specPayment.type.eq(PaymentType.FTLS))
            .then(specPayment.projectLumpSum.paymentEnabledDate)
            .otherwise(specPayment.projectReport.verificationEndDate)

        "ecId" -> specPaymentToEcExtensionEntity.paymentApplicationToEc?.id
        else -> specPayment.id
    }

    return OrderSpecifier(if (orderBy.isAscending) Order.ASC else Order.DESC, dfg)
}

fun QueryResults<Tuple>.toPageResult(pageable: Pageable) = PageImpl(
    results.map { it: Tuple ->
        PaymentToProjectTmp(
            payment = it.get(0, PaymentEntity::class.java)!!,
            amountPaid = it.get(1, BigDecimal::class.java),
            amountAuthorized = it.get(2, BigDecimal::class.java),
            lastPaymentDate = it.get(3, LocalDate::class.java),
            totalEligibleForRegular = it.get(4, BigDecimal::class.java),
            code = it.get(5, String::class.java),
            paymentToEcExtension = PaymentToEcExtensionTmp(
                paymentToEcId = it.get(6, Long::class.java),
                partnerContribution = it.get(7, BigDecimal::class.java)!!,
                publicContribution = it.get(8, BigDecimal::class.java)!!,
                correctedPublicContribution = it.get(9, BigDecimal::class.java)!!,
                autoPublicContribution = it.get(10, BigDecimal::class.java)!!,
                correctedAutoPublicContribution = it.get(11, BigDecimal::class.java)!!,
                privateContribution = it.get(12, BigDecimal::class.java)!!,
                correctedPrivateContribution = it.get(13, BigDecimal::class.java)!!,
            ),
        )
    },
    pageable,
    total,
)

fun PaymentSearchRequest.transformToWhereClause(
    qPayment: QPaymentEntity,
    qProjectLumpSum: QProjectLumpSumEntity,
    qProjectReport: QProjectReportEntity,
    specProjectContracting: QProjectContractingMonitoringEntity,
    specPaymentToEcExtension: QPaymentToEcExtensionEntity,
): BooleanExpression? {
    val expressions = mutableListOf<BooleanExpression>()

    if (this.paymentId != null)
        expressions.add(qPayment.id.eq(this.paymentId))

    if (this.paymentType != null)
        expressions.add(qPayment.type.eq(this.paymentType))

    val projectIds = this.projectIdentifiers.mapNotNull { it.toLongOrNull() }
    val projectIdentifiers = this.projectIdentifiers.filter { it.isNotBlank() }

    val projectIdExpressions = mutableListOf<BooleanExpression>()
    if (projectIds.isNotEmpty())
        projectIdExpressions.add(qPayment.project.id.`in`(projectIds))
    if (projectIdentifiers.isNotEmpty())
        projectIdExpressions.add(qPayment.projectCustomIdentifier.`in`(projectIdentifiers))
    if (projectIdExpressions.isNotEmpty())
        expressions.add(projectIdExpressions.reduce { f, s -> f.or(s) })

    if (!this.projectAcronym.isNullOrBlank())
        expressions.add(qPayment.projectAcronym.containsIgnoreCase(this.projectAcronym))

    val submissionDateFrom = this.claimSubmissionDateFrom?.atTime(LocalTime.MIN)?.atZone(ZoneId.systemDefault())
    val submissionDateTo = this.claimSubmissionDateTo?.atTime(LocalTime.MAX)?.atZone(ZoneId.systemDefault())
    if (submissionDateFrom != null)
        expressions.add(qPayment.project.contractedDecision.updated.goe(submissionDateFrom))
    if (submissionDateTo != null)
        expressions.add(qPayment.project.contractedDecision.updated.loe(submissionDateTo))

    val paymentApprovalDateFrom = this.approvalDateFrom?.atTime(LocalTime.MIN)?.atZone(ZoneId.systemDefault())
    val paymentApprovalDateTo = this.approvalDateTo?.atTime(LocalTime.MAX)?.atZone(ZoneId.systemDefault())
    if (paymentApprovalDateFrom != null)
        expressions.add(
            listOf(
                qProjectLumpSum.paymentEnabledDate.goe(paymentApprovalDateFrom),
                qProjectReport.verificationEndDate.goe(paymentApprovalDateFrom),
            ).joinWithOr()
        )
    if (paymentApprovalDateTo != null)
        expressions.add(
            listOf(
                qProjectLumpSum.paymentEnabledDate.loe(paymentApprovalDateTo),
                qProjectReport.verificationEndDate.loe(paymentApprovalDateTo),
            ).joinWithOr()
        )

    if (fundIds.isNotEmpty())
        expressions.add(qPayment.fund.id.`in`(this.fundIds))

    if (ecPaymentIds.isNotEmpty()) {
        val ids = ecPaymentIds.filterNotNull()
        if (ids.isEmpty())
            expressions.add(specPaymentToEcExtension.paymentApplicationToEc.isNull)
        else if (ecPaymentIds.size == ids.size)
            expressions.add(specPaymentToEcExtension.paymentApplicationToEc.id.`in`(ids))
        else
            expressions.add(
                listOf(
                    specPaymentToEcExtension.paymentApplicationToEc.isNull,
                    specPaymentToEcExtension.paymentApplicationToEc.id.`in`(ids),
                ).joinWithOr()
            )
    }

    if (contractingScoBasis != null) {
        val allAnswersNo = specProjectContracting.notFlagged()
        val scoBasisFilter = when (contractingScoBasis) {
            DoesNotFallUnderArticle94Nor95 -> allAnswersNo
            FallsUnderArticle94Or95 -> allAnswersNo.not()
        }
        expressions.add(scoBasisFilter)
    }
    if (finalScoBasis != null) {
        expressions.add(specPaymentToEcExtension.finalScoBasis.eq(finalScoBasis))
    }

    return expressions.joinWithAnd()
}

fun QProjectContractingMonitoringEntity.notFlagged() =
    typologyProv94.isNull().or(typologyProv94.eq(No)).and(typologyProv95.isNull().or(typologyProv95.eq(No)))

fun Collection<BooleanExpression>.joinWithOr() = reduce { f, s -> f.or(s) }
fun Collection<BooleanExpression>.joinWithAnd() =
    if (isEmpty()) null else reduce { f, s -> f.and(s) }

fun PaymentSearchRequest.transformToHavingClause(specPaymentPartnerInstallment: QPaymentPartnerInstallmentEntity): BooleanExpression? {
    val expressions = mutableListOf<BooleanExpression>()

    if (this.lastPaymentDateFrom != null)
        expressions.add(specPaymentPartnerInstallment.paymentDate.max().goe(this.lastPaymentDateFrom))
    if (this.lastPaymentDateTo != null)
        expressions.add(specPaymentPartnerInstallment.paymentDate.max().loe(this.lastPaymentDateTo))

    return if (expressions.isNotEmpty()) expressions.reduce { f, s -> f.and(s) } else null
}
