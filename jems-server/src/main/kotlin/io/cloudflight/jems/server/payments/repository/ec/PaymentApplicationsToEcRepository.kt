package io.cloudflight.jems.server.payments.repository.ec

import io.cloudflight.jems.server.payments.entity.PaymentApplicationsToEcEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentApplicationsToEcRepository: JpaRepository<PaymentApplicationsToEcEntity, Long>
