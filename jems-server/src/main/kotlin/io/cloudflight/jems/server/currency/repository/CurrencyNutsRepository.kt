package io.cloudflight.jems.server.currency.repository

import io.cloudflight.jems.server.currency.entity.CurrencyNuts
import io.cloudflight.jems.server.currency.entity.CurrencyNutsId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyNutsRepository : JpaRepository<CurrencyNuts, CurrencyNutsId> {

    fun getByIdNutsId(nutsId: String): CurrencyNuts?

}
