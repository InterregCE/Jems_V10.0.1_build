package io.cloudflight.jems.server.payments.repository.account

import io.cloudflight.jems.server.payments.entity.account.PaymentAccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentAccountRepository: JpaRepository<PaymentAccountEntity, Long> {

    fun deleteAllByProgrammeFundIdIn(ids: Set<Long>)

}
