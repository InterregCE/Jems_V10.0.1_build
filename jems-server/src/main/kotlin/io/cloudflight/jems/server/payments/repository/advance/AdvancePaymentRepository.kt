package io.cloudflight.jems.server.payments.repository.advance

import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdvancePaymentRepository: JpaRepository<AdvancePaymentEntity, Long>
