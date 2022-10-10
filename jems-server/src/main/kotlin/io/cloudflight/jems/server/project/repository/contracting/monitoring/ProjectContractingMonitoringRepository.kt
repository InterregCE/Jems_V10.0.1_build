package io.cloudflight.jems.server.project.repository.contracting.monitoring

import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingMonitoringEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProjectContractingMonitoringRepository: JpaRepository<ProjectContractingMonitoringEntity, Long> {

    fun findByProjectId(projectId: Long): Optional<ProjectContractingMonitoringEntity>

    @Query("""
        SELECT CASE WHEN COUNT(ppi) > 0 THEN TRUE ELSE FALSE END
            FROM payment_partner_installment ppi, payment_partner pp, payment pay
            WHERE pay.project.id = :projectId AND pay.programmeLumpSumId = :lumpSumId AND pay.orderNr = :orderNr
                AND pay.id = pp.payment.id
                AND pp.id = ppi.paymentPartner.id
                AND ppi.isSavePaymentInfo = TRUE
    """)
    fun existsSavedInstallment(projectId: Long, lumpSumId: Long, orderNr: Int): Boolean

}
