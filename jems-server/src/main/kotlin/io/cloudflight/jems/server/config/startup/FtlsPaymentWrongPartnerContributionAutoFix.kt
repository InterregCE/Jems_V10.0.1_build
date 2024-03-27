package io.cloudflight.jems.server.config.startup

import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.notification.inApp.service.project.GlobalProjectNotificationServiceInteractor
import io.cloudflight.jems.server.payments.entity.QPaymentEntity
import io.cloudflight.jems.server.payments.entity.QPaymentToEcExtensionEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.project.entity.lumpsum.QProjectLumpSumEntity
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

@Component
class FtlsPaymentWrongPartnerContributionAutoFix(
    private val notificationProjectService: GlobalProjectNotificationServiceInteractor,
    private val jpaQueryFactory: JPAQueryFactory,
) : ApplicationListener<ApplicationReadyEvent> {

    companion object {
        private val logger = LoggerFactory.getLogger(FtlsPaymentWrongPartnerContributionAutoFix::class.java)
        private val identifier = UUID.fromString("54cebb90-fed0-464a-a8ac-2682881130cb")

        private fun subject(amount: Int) = "Jems v10.0 Change of partner contributions in FTLS done - $amount issues need your attention!"

        private val bodyWithoutIssues = """
            |An automatic update has been performed to fix wrong partner contributions, but it did not find any issues
            | (no any partner contribution needed change).

            |You do not have to perform any action and you can continue using Jems.
        """.trimMargin()

        private fun bodyWithIssues(listOfIssues: String) = """
            |A check was performed to ensure that the co-financing rates are correct for the FTLS that are set to YES ready for payment for your projects.

            |The issue is fixed for any FTLS that were set to Yes when v9.0 is installed. Please, read Jems v10.0 release notes
            |(https://jems.interact-eu.net/manual) for further guidance on how to proceed with the detected issues.

            |System found following FTLS suffering from a risk of wrong partner contribution and therefore a total eligible amount
            |(if list is too long to show, complete list is in system log)

            |-----START-----
            |$listOfIssues
            |------END------
        """.trimMargin()
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        logger.info("Performing check for automatically fixed FTLS payments - partner contribution")
        val updatedContributions = fetchUpdatedContributions()

        val listOfUpdates = updatedContributions.toNiceFormat()
        logger.info(subject(updatedContributions.size))
        logger.info(listOfUpdates)
        sendNotification(
            subject(updatedContributions.size),
            if (updatedContributions.isEmpty()) bodyWithoutIssues else bodyWithIssues(listOfUpdates),
        )
    }

    private fun fetchUpdatedContributions(): List<FtlsPaymentWithChangedPartnerContribution> {
        val projectLumpSum = QProjectLumpSumEntity.projectLumpSumEntity
        val payment = QPaymentEntity.paymentEntity
        val paymentExtension = QPaymentToEcExtensionEntity.paymentToEcExtensionEntity

        return jpaQueryFactory.select(
            payment.project.id,
            payment.projectCustomIdentifier,
            payment.projectAcronym,
            payment.id,
            payment.projectLumpSum.id.orderNr,
            projectLumpSum.paymentEnabledDate,
            paymentExtension.beforeFixPartnerContribution,
            paymentExtension.partnerContribution,
        ).from(paymentExtension)
            .leftJoin(payment)
                .on(payment.eq(paymentExtension.payment))
            .leftJoin(projectLumpSum)
                .on(projectLumpSum.eq(payment.projectLumpSum))
            .where(
                paymentExtension.partnerContribution.ne(paymentExtension.beforeFixPartnerContribution)
                    .and(payment.type.eq(PaymentType.FTLS))
            )
            .fetch()
            .map {
                FtlsPaymentWithChangedPartnerContribution(
                    projectId = it.get(0, Long::class.java)!!,
                    projectCustomIdentifier = it.get(1, String::class.java)!!,
                    projectAcronym = it.get(2, String::class.java)!!,
                    paymentId = it.get(3, Long::class.java)!!,
                    lumpSumOrderNr = it.get(4, Int::class.java),
                    paymentEnabledDate = it.get(5, ZonedDateTime::class.java),
                    oldPartnerContribution = it.get(6, BigDecimal::class.java),
                    newPartnerContribution = it.get(7, BigDecimal::class.java)!!,
                )
            }
    }

    private fun sendNotification(subject: String, body: String) {
        notificationProjectService.sendSystemNotification(subject, body, identifier)
    }

    private fun List<FtlsPaymentWithChangedPartnerContribution>.toNiceFormat(): String {
        val result = StringBuilder("")
        val grouped = groupBy { it.projectId }.mapValues { it.value.groupBy { it.lumpSumOrderNr } }.toSortedMap()
        grouped.forEach {
            val projectCustomIdentifier = it.value.values.first()[0].projectCustomIdentifier
            val projectAcronym = it.value.values.first()[0].projectAcronym
            result.appendLine("Project id=${it.key} - $projectCustomIdentifier '${projectAcronym}':")
            it.value.forEach { (orderNr, payments) ->
                result.appendLine("  Lump sum ${orderNr ?: "N/A"}:")
                payments.forEach {
                    result.appendLine("    Payment ${it.paymentId}: old partner contribution ${it.oldPartnerContribution}" +
                            " to new partner contribution ${it.newPartnerContribution}")
                }
            }
        }
        return result.toString()
    }

    data class FtlsPaymentWithChangedPartnerContribution(
        val projectId: Long,
        val projectCustomIdentifier: String,
        val projectAcronym: String,
        val paymentId: Long,
        val lumpSumOrderNr: Int?,
        val paymentEnabledDate: ZonedDateTime?,
        val oldPartnerContribution: BigDecimal?,
        val newPartnerContribution: BigDecimal,
    )

}
