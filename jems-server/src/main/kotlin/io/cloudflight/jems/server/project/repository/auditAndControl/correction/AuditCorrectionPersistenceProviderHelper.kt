package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import io.cloudflight.jems.server.payments.entity.QPaymentToEcCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.entity.account.QPaymentAccountCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionSearchRequest
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionSearchRequest
import io.cloudflight.jems.server.payments.repository.regular.joinWithAnd
import io.cloudflight.jems.server.payments.repository.regular.joinWithOr
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionPersistenceProvider.Companion.accountCorrection
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionPersistenceProvider.Companion.audit
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionPersistenceProvider.Companion.correctionExtensionToAccount
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionPersistenceProvider.Companion.correctionExtensionToEc
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionPersistenceProvider.Companion.correctionProgrammeMeasure
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionPersistenceProvider.Companion.project
import org.springframework.data.domain.Sort

fun PaymentToEcCorrectionSearchRequest.transformToWhereClause(
    qCorrection: QAuditControlCorrectionEntity,
    qCorrectionProgrammeMeasure: QAuditControlCorrectionMeasureEntity,
    qPaymentToEcCorrectionExtension: QPaymentToEcCorrectionExtensionEntity,
): BooleanExpression? {
    val expressions = mutableListOf<BooleanExpression>()

    expressions.add(qCorrection.status.eq(correctionStatus))
    expressions.add(qCorrectionProgrammeMeasure.scenario.`in`(scenarios))

    if (ecPaymentIds.isNotEmpty()) {
        val ids = ecPaymentIds.filterNotNull()
        if (ids.isEmpty())
            expressions.add(qPaymentToEcCorrectionExtension.paymentApplicationToEc.isNull)
        else if (ecPaymentIds.size == ids.size)
            expressions.add(qPaymentToEcCorrectionExtension.paymentApplicationToEc.id.`in`(ids))
        else
            expressions.add(
                listOf(
                    qPaymentToEcCorrectionExtension.paymentApplicationToEc.isNull,
                    qPaymentToEcCorrectionExtension.paymentApplicationToEc.id.`in`(ids),
                ).joinWithOr()
            )
    }

    if (fundIds.isNotEmpty())
        expressions.add(qCorrection.programmeFund.id.`in`(this.fundIds))

    return expressions.joinWithAnd()
}

fun PaymentAccountCorrectionSearchRequest.transformToWhereClause(
    qCorrection: QAuditControlCorrectionEntity,
    qCorrectionProgrammeMeasure: QAuditControlCorrectionMeasureEntity,
    qPaymentAccountCorrectionExtension: QPaymentAccountCorrectionExtensionEntity
): BooleanExpression? {
    val expressions = mutableListOf<BooleanExpression>()

    expressions.add(qCorrection.status.eq(correctionStatus))
    expressions.add(qCorrectionProgrammeMeasure.scenario.`in`(scenarios))

    if (paymentAccountIds.isNotEmpty()) {
        val ids = paymentAccountIds.filterNotNull()
        if (ids.isEmpty())
            expressions.add(qPaymentAccountCorrectionExtension.paymentAccount.isNull)
        else if (paymentAccountIds.size == ids.size)
            expressions.add(qPaymentAccountCorrectionExtension.paymentAccount.id.`in`(ids))
        else
            expressions.add(
                listOf(
                    qPaymentAccountCorrectionExtension.paymentAccount.isNull,
                    qPaymentAccountCorrectionExtension.paymentAccount.id.`in`(ids),
                ).joinWithOr()
            )
    }

    if (fundIds.isNotEmpty())
        expressions.add(qCorrection.programmeFund.id.`in`(this.fundIds))

    return expressions.joinWithAnd()
}


fun Sort.toQueryDslOrderByForCorrection(): OrderSpecifier<*> {
    val orderBy = if (isSorted) this.get().findFirst().get() else Sort.Order.desc("id")

    val sortingColumn = when (orderBy.property) {
        "projectCustomIdentifier" -> project.customIdentifier
        "projectAcronym" -> project.acronym
        "scenario" -> correctionProgrammeMeasure.scenario
        "controllingBody" -> audit.controllingBody

        // if linked to EcPayment
        "ecPaymentId" -> correctionExtensionToEc.paymentApplicationToEc.id
        // if linked to Account
        "paymentAccountId" -> correctionExtensionToAccount.paymentAccount.id

        "id" -> accountCorrection.id
        else -> accountCorrection.id
    }

    return OrderSpecifier(if (orderBy.isAscending) Order.ASC else Order.DESC, sortingColumn)
}
