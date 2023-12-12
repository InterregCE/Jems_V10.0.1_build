package io.cloudflight.jems.server.payments.repository.advance

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.payments.entity.QAdvancePaymentEntity
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.repository.regular.joinWithAnd
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.LocalDate

fun QueryResults<Tuple>.toPageResult(pageable: Pageable): PageImpl<AdvancePayment> =
    PageImpl(
        results.map {
            AdvancePayment(
                id = it.get(0, Long::class.java)!!,
                projectCustomIdentifier = it.get(1, String::class.java)!!,
                projectAcronym = it.get(2,  String::class.java)!!,
                partnerType = it.get(3, ProjectPartnerRole::class.java)!!,
                partnerSortNumber =  it.get(4, Int::class.java)!!,
                partnerAbbreviation = it.get(5, String::class.java)!!,
                paymentAuthorized = it.get(6, Boolean::class.java),
                amountPaid = it.get(7, BigDecimal::class.java),
                amountSettled  = it.get(8, BigDecimal::class.java),
                paymentDate = it.get(9, LocalDate::class.java),
                programmeFund = it.get(10, ProgrammeFundEntity::class.java)?.toModel(),
                partnerContribution = idNamePairOrNull(
                    it.get(11, Long::class.java),
                    it.get(12, String::class.java)
                ),
                partnerContributionSpf  = idNamePairOrNull(
                    it.get(13, Long::class.java),
                    it.get(14, String::class.java)
                ),
                paymentSettlements  = emptyList()
            )
        },
        pageable,
        total
    )

fun AdvancePaymentSearchRequest.transformToWhereClause(spec :QAdvancePaymentEntity):  BooleanExpression? {
    val expressions = mutableListOf<BooleanExpression>()
    val filters = this

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

    return expressions.joinWithAnd()
}


fun Sort.toQueryDslOrderBy(): OrderSpecifier<*> {
    val orderBy = if (isSorted) this.get().findFirst().get() else Sort.Order.desc("id")

    val specAdvancePayment = QAdvancePaymentEntity.advancePaymentEntity
    val dfg = when (orderBy.property) {
        "projectCustomIdentifier" -> specAdvancePayment.projectCustomIdentifier
        "projectAcronym" -> specAdvancePayment.projectAcronym
        "partnerAbbreviation" -> specAdvancePayment.partnerAbbreviation
        "programmeFund.id" -> specAdvancePayment.programmeFund.id
        "paymentDate" -> specAdvancePayment.paymentDate
        else -> specAdvancePayment.id
    }

    return OrderSpecifier(if (orderBy.isAscending) Order.ASC else Order.DESC, dfg)
}

private fun idNamePairOrNull(id: Long?, name: String?): IdNamePair? {
    return if (id != null && name != null) {
        IdNamePair(id, name)
    } else null
}
