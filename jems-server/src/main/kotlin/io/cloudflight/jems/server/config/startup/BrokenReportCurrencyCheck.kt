package io.cloudflight.jems.server.config.startup

import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.currency.entity.QCurrencyRateEntity
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.project.entity.QProjectEntity
import io.cloudflight.jems.server.project.entity.partner.QProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.report.partner.QProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.QPartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

@Component
class BrokenReportCurrencyCheck(
    private val notificationProjectService: GlobalProjectNotificationServiceInteractor,
    private val jpaQueryFactory: JPAQueryFactory,
) : ApplicationListener<ApplicationReadyEvent> {

    companion object {
        private val logger = LoggerFactory.getLogger(BrokenReportCurrencyCheck::class.java)
        private val identifier = UUID.randomUUID()

        private const val successSubject = "Jems v8.0.5 - Check for errors in expenditures done - No errors detected"
        private val successBody = """
            |A check was performed to ensure submitted currency rates for all expenditures are matching currencies from
            |that particular month.

            |We did not detect any issue, so you do not have to perform any action and you can continue using Jems.
        """.trimMargin()

        private fun errorSubject(amount: Int) = "Jems v8.0.5 - Check for errors in expenditures done - $amount issues needs your attention!"
        private fun errorBody(listOfIssues: String) = """
            |A check was performed to ensure submitted currency rates for all expenditures are matching currencies from
            |that particular month.

            |Unfortunately, we detected that during resubmission of some partner reports, those currency rates got broken.

            |Please, read Jems v8.0.5 release notes (jems.interact-eu.net/manual) for further guidance on how to proceed with the detected issues.

            |System found following expenditures suffering from conversion-rate bug:
            |(if list is too long to show, complete list is in system log)

            |-----START-----
            |$listOfIssues
            |------END------
        """.trimMargin()
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        logger.info("Performing check for broken currency rates inside partner report - list of expenditures")
        val broken = fetchBroken()

        if (broken.isEmpty()) {
            logger.info("Check finished - no broken expenditures detected.")
            sendNotification(successSubject, successBody)
            return
        }

        val listOfProblems = broken.toNiceFormat()
        logger.error(errorSubject(broken.size))
        logger.error(listOfProblems)
        sendNotification(errorSubject(broken.size), errorBody(listOfProblems))
    }

    private fun sendNotification(subject: String, body: String) {
        notificationProjectService.sendSystemNotification(subject, body, identifier)
    }

    private fun fetchBroken(): List<ExpenditureWithBrokenRate> {
        val expenditure = QPartnerReportExpenditureCostEntity.partnerReportExpenditureCostEntity
        val report = QProjectPartnerReportEntity.projectPartnerReportEntity
        val currencyRate = QCurrencyRateEntity.currencyRateEntity
        val partner = QProjectPartnerEntity.projectPartnerEntity
        val project = QProjectEntity.projectEntity

        return jpaQueryFactory.select(
            project.id,
            project.customIdentifier,
            project.acronym,
            partner.role,
            partner.sortNumber,
            partner.abbreviation,
            report.number,
            expenditure.number,
            expenditure.currencyCode,
            expenditure.currencyConversionRate,
            currencyRate.conversionRate,
            expenditure.declaredAmountAfterSubmission,
            expenditure.declaredAmount,
        ).from(expenditure)
            .leftJoin(report)
                .on(expenditure.partnerReport.eq(report))
            .leftJoin(currencyRate)
                .on(report.firstSubmission.year().eq(currencyRate.id.year)
                    .and(report.firstSubmission.month().eq(currencyRate.id.month))
                    .and(expenditure.currencyCode.eq(currencyRate.id.code)))
            .leftJoin(partner)
                .on(report.partnerId.eq(partner.id))
            .leftJoin(project)
                .on(partner.project.eq(project))
            .where(
                (expenditure.currencyConversionRate.ne(currencyRate.conversionRate)
                    .or(expenditure.declaredAmountAfterSubmission.ne(expenditure.declaredAmount.divide(currencyRate.conversionRate).roundHalfUp())))
                .and(expenditure.reIncludedFromExpenditure.isNull())
            ).fetch()
            .map { it: Tuple ->
                val declaredAmount = it.get(12, BigDecimal::class.java)!!
                val correctRate = it.get(10, BigDecimal::class.java)!!
                ExpenditureWithBrokenRate(
                    projectId = it.get(0, Long::class.java)!!,
                    projectCustomIdentifier = it.get(1, String::class.java)!!,
                    projectAcronym = it.get(2, String::class.java)!!,
                    partnerRole = it.get(3, ProjectPartnerRole::class.java)!!,
                    partnerNr = it.get(4, Int::class.java)!!,
                    partnerAbbreviation = it.get(5, String::class.java)!!,
                    reportNr = it.get(6, Int::class.java)!!,
                    expenditureNumber = it.get(7, Int::class.java)!!,
                    currencyCode = it.get(8, String::class.java)!!,
                    wrongRate = it.get(9, BigDecimal::class.java)!!,
                    correctRate = correctRate,
                    wrongAmount = it.get(11, BigDecimal::class.java)!!,
                    correctAmount = declaredAmount.divide(correctRate, 2, RoundingMode.HALF_UP),
                )
            }
    }

    private fun NumberExpression<BigDecimal>.roundHalfUp(): NumberExpression<BigDecimal> =
        multiply(100).round().divide(100)

    private fun List<ExpenditureWithBrokenRate>.toNiceFormat(): String {
        val byReport = groupBy { it.projectId }
            .mapValues {
                it.value.groupBy { it.partnerNr }
                    .mapValues { it.value.groupBy { it.reportNr } }
            }
        val result = StringBuilder()

        byReport.forEach { (_, projectItems) ->
            val projectItem = projectItems.values.first().values.first().first()
            result.appendLine("Project ${projectItem.projectCustomIdentifier} '${projectItem.projectAcronym}':")

            projectItems.forEach { (partnerNr, partnerItems) ->
                val partnerItem = partnerItems.values.first().first()
                result.appendLine("  ${if (partnerItem.partnerRole == ProjectPartnerRole.LEAD_PARTNER) "LP" else "PP"}$partnerNr"
                        + " '${partnerItem.partnerAbbreviation}':")

                partnerItems.forEach { (reportNr, reportItems) ->
                    reportItems.forEach { expenditure ->
                        result.appendLine("    R${reportNr}.${expenditure.expenditureNumber} - ${expenditure.currencyCode} rate is" +
                                " ${expenditure.wrongRate} but should be ${expenditure.correctRate} and declared amount is" +
                                " ${expenditure.wrongAmount} but should be ${expenditure.correctAmount}")
                    }
                }
            }
            result.appendLine()
        }
        return result.toString()
    }

    data class ExpenditureWithBrokenRate(
        val projectId: Long,
        val projectCustomIdentifier: String,
        val projectAcronym: String,

        val partnerRole: ProjectPartnerRole,
        val partnerNr: Int,
        val partnerAbbreviation: String,

        val reportNr: Int,

        val expenditureNumber: Int,
        val currencyCode: String,
        val wrongRate: BigDecimal,
        val correctRate: BigDecimal,
        val wrongAmount: BigDecimal,
        val correctAmount: BigDecimal,
    )

}
