package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.entity.PaymentToEcExtensionEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentToEcExtensionRepository: JpaRepository<PaymentToEcExtensionEntity, Long> {

    fun findAllByPaymentApplicationToEcId(ecPaymentId: Long): List<PaymentToEcExtensionEntity>

    fun findAllByPaymentApplicationToEcNotNullAndPaymentProjectReportId(projectReportId: Long): List<PaymentToEcExtensionEntity>


    @Query(
        """
           SELECT new kotlin.Pair(paymentExtension.payment.projectLumpSum.id.orderNr, paymentExtension.paymentApplicationToEc.id )
            FROM  #{#entityName} AS paymentExtension
            WHERE paymentExtension.payment.project.id=:projectId
                AND paymentExtension.payment.type = 'FTLS'
                AND paymentExtension.paymentApplicationToEc != null
        """
    )
    fun findAllEcPaymentIdsAndLinkedFTLSPaymentByProjectId(projectId: Long): List<Pair<Int, Long>>

}
