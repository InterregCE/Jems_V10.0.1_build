package io.cloudflight.jems.server.currency.repository

import io.cloudflight.jems.server.currency.entity.CurrencyNutsEntity
import io.cloudflight.jems.server.currency.entity.CurrencyNutsIdEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyNutsRepository : JpaRepository<CurrencyNutsEntity, CurrencyNutsIdEntity> {

    fun getByIdNutsId(nutsId: String): CurrencyNutsEntity?

}
