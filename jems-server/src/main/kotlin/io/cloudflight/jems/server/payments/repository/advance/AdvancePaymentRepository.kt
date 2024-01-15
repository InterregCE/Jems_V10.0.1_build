package io.cloudflight.jems.server.payments.repository.advance

import io.cloudflight.jems.server.payments.entity.AdvancePaymentEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface AdvancePaymentRepository: JpaRepository<AdvancePaymentEntity, Long>, QuerydslPredicateExecutor<AdvancePaymentEntity> {

    fun findAllByProjectId(projectId: Long): List<AdvancePaymentEntity>

    fun findAllByProjectIdAndIsPaymentConfirmedTrue(projectId: Long, pageable: Pageable): Page<AdvancePaymentEntity>
}
