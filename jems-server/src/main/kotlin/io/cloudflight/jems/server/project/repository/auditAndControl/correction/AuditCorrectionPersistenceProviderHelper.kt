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
import io.cloudflight.jems.server.programme.entity.QProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlEntity
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

    val specCorrection = QAuditControlCorrectionEntity.auditControlCorrectionEntity
    val specPaymentToEcCorrectionExtensionEntity = QPaymentToEcCorrectionExtensionEntity.paymentToEcCorrectionExtensionEntity
    val specProject = QProjectEntity.projectEntity

    val specProjectAuditControl = QAuditControlEntity.auditControlEntity
    val specCorrectionProgrammeMeasure =
        QAuditControlCorrectionMeasureEntity.auditControlCorrectionMeasureEntity
    val specProgrammeSpecificObjectiveEntity = QProgrammeSpecificObjectiveEntity.programmeSpecificObjectiveEntity

    val sortingColumn = when (orderBy.property) {
        "projectCustomIdentifier" -> specProject.customIdentifier
        "projectAcronym" -> specProject.acronym
        "id" -> specCorrection.id
        "priorityAxis" -> specProgrammeSpecificObjectiveEntity.code
        "scenario" -> specCorrectionProgrammeMeasure.scenario
        "controllingBody" -> specProjectAuditControl.controllingBody
        "ecId" -> specPaymentToEcCorrectionExtensionEntity.paymentApplicationToEc?.id
        else -> specCorrection.id
    }

    return OrderSpecifier(if (orderBy.isAscending) Order.ASC else Order.DESC, sortingColumn)
}
