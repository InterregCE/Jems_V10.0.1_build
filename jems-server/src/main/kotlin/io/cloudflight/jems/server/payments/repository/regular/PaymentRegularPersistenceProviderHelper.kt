package io.cloudflight.jems.server.payments.repository.regular

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentPartnerInstallmentEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentToProjectTmp
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
    val dfg = when (orderBy.property) {
        "type" -> specPayment.type
        "projectCustomIdentifier" -> specPayment.projectCustomIdentifier
        "projectAcronym" -> specPayment.projectAcronym
        "project.contractedDecision.updated" -> specPayment.project.contractedDecision.updated
        "fund.type" -> specPayment.fund.type
        "id" -> specPayment.id
        "projectReport.number" -> specPayment.projectReport?.number
        "projectReport.verificationEndDate" -> specPayment.projectReport?.verificationEndDate
        else -> specPayment.id
    }

    return OrderSpecifier(if (orderBy.isAscending) Order.ASC else Order.DESC, dfg)
}

fun QueryResults<Tuple>.toPageResult(pageable: Pageable) = PageImpl(
    results.map { it: Tuple -> PaymentToProjectTmp(
        payment = it.get(0, PaymentEntity::class.java)!!,
        amountPaid = it.get(1, BigDecimal::class.java),
        amountAuthorized  = it.get(2, BigDecimal::class.java),
        lastPaymentDate = it.get(3, LocalDate::class.java),
        totalEligibleForRegular = it.get(4, BigDecimal::class.java)
    ) },
    pageable,
    total,
)

fun PaymentSearchRequest.transformToWhereClause(): BooleanExpression? {
    val spec = QPaymentEntity.paymentEntity
    val expressions = mutableListOf<BooleanExpression>()

    if (this.paymentId != null)
        expressions.add(spec.id.eq(this.paymentId))

    if (this.paymentType != null)
        expressions.add(spec.type.eq(this.paymentType))

    val projectIds = this.projectIdentifiers.mapNotNull { it.toLongOrNull() }
    val projectIdentifiers = this.projectIdentifiers.filter { it.isNotBlank() }

    val projectIdExpressions = mutableListOf<BooleanExpression>()
    if (projectIds.isNotEmpty())
        projectIdExpressions.add(spec.project.id.`in`(projectIds))
    if (projectIdentifiers.isNotEmpty())
        projectIdExpressions.add(spec.projectCustomIdentifier.`in`(projectIdentifiers))
    if (projectIdExpressions.isNotEmpty())
        expressions.add(projectIdExpressions.reduce { f, s -> f.or(s) })

    if (!this.projectAcronym.isNullOrBlank())
        expressions.add(spec.projectAcronym.containsIgnoreCase(this.projectAcronym))

    val submissionDateFrom = this.claimSubmissionDateFrom?.atTime(LocalTime.MIN)?.atZone(ZoneId.systemDefault())
    val submissionDateTo = this.claimSubmissionDateTo?.atTime(LocalTime.MAX)?.atZone(ZoneId.systemDefault())
    if (submissionDateFrom != null)
        expressions.add(spec.project.contractedDecision.updated.goe(submissionDateFrom))
    if (submissionDateTo != null)
        expressions.add(spec.project.contractedDecision.updated.loe(submissionDateTo))

    val paymentApprovalDateFrom = this.approvalDateFrom?.atTime(LocalTime.MIN)?.atZone(ZoneId.systemDefault())
    val paymentApprovalDateTo = this.approvalDateTo?.atTime(LocalTime.MAX)?.atZone(ZoneId.systemDefault())
    if (paymentApprovalDateFrom != null)
        expressions.add(spec.projectLumpSum.paymentEnabledDate.goe(paymentApprovalDateFrom))
    if (paymentApprovalDateTo != null)
        expressions.add(spec.projectLumpSum.paymentEnabledDate.loe(paymentApprovalDateTo))

    if (this.fundIds.isNotEmpty())
        expressions.add(spec.fund.id.`in`(this.fundIds))

    return if (expressions.isNotEmpty()) expressions.reduce { f, s -> f.and(s) } else null
}

fun PaymentSearchRequest.transformToHavingClause(): BooleanExpression? {
    val specPaymentPartnerInstallment = QPaymentPartnerInstallmentEntity.paymentPartnerInstallmentEntity
    val expressions = mutableListOf<BooleanExpression>()

    if (this.lastPaymentDateFrom != null)
        expressions.add(specPaymentPartnerInstallment.paymentDate.max().goe(this.lastPaymentDateFrom))
    if (this.lastPaymentDateTo != null)
        expressions.add(specPaymentPartnerInstallment.paymentDate.max().loe(this.lastPaymentDateTo))

    return if (expressions.isNotEmpty()) expressions.reduce { f, s -> f.and(s) } else null
}
