package io.cloudflight.jems.server.payments.repository.advance

import com.querydsl.core.types.dsl.BooleanExpression
import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import io.cloudflight.jems.server.payments.entity.QAdvancePaymentEntity
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface AdvancePaymentRepository: JpaRepository<AdvancePaymentEntity, Long>, QuerydslPredicateExecutor<AdvancePaymentEntity> {

    fun findAllByProjectId(projectId: Long): List<AdvancePaymentEntity>
}

fun AdvancePaymentRepository.filterPayments(
    pageable: Pageable,
    filters: AdvancePaymentSearchRequest,
): Page<AdvancePaymentEntity> {
    val spec = QAdvancePaymentEntity.advancePaymentEntity
    val expressions = mutableListOf<BooleanExpression>()

    if (filters.paymentId != null)
        expressions.add(spec.id.eq(filters.paymentId))

    val projectIds = filters.projectIdentifiers.mapNotNull { it.toLongOrNull() }
    val projectIdentifiers = filters.projectIdentifiers.filter { it.isNotBlank() }

    val projectIdExpressions = mutableListOf<BooleanExpression>()
    if (projectIds.isNotEmpty())
        projectIdExpressions.add(spec.projectId.`in`(projectIds))
    if (projectIdentifiers.isNotEmpty())
        projectIdExpressions.add(spec.projectCustomIdentifier.`in`(projectIdentifiers))
    if (projectIdExpressions.isNotEmpty())
        expressions.add(projectIdExpressions.reduce { f, s -> f.or(s) })

    if (!filters.projectAcronym.isNullOrBlank())
        expressions.add(spec.projectAcronym.containsIgnoreCase(filters.projectAcronym))

    if (filters.fundIds.isNotEmpty())
        expressions.add(spec.programmeFund.id.`in`(filters.fundIds))

    if (filters.amountFrom != null)
        expressions.add(spec.amountPaid.goe(filters.amountFrom))
    if (filters.amountTo != null)
        expressions.add(spec.amountPaid.loe(filters.amountTo))

    if (filters.dateFrom != null)
        expressions.add(spec.paymentDate.goe(filters.dateFrom))
    if (filters.dateTo != null)
        expressions.add(spec.paymentDate.loe(filters.dateTo))

    if (filters.authorized != null)
        expressions.add(spec.isPaymentAuthorizedInfo.eq(filters.authorized))
    if (filters.confirmed != null)
        expressions.add(spec.isPaymentConfirmed.eq(filters.confirmed))

    return if (expressions.isNotEmpty())
        findAll(expressions.reduce { f, s -> f.and(s) }, pageable)
    else
        findAll(pageable)
}
