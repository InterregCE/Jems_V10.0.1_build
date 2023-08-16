package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentApplicationsToEcRepository: JpaRepository<PaymentApplicationToEcEntity, Long>
