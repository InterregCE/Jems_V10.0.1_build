package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumPerPartnerSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
interface ProjectLumpSumRepository : CrudRepository<ProjectPartnerLumpSumEntity, UUID> {

    @Query("SELECT new io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumPerPartnerSumEntity(e.id.projectPartner, SUM(e.amount)) FROM #{#entityName} e WHERE e.id.projectLumpSumId IN :ids GROUP BY e.id.projectPartner")
    fun sumLumpSumsPerPartner(@Param("ids") lumpSumIds: Set<UUID>): List<ProjectLumpSumPerPartnerSumEntity>

    @Query("SELECT  SUM(e.amount) FROM #{#entityName} e WHERE e.id.projectPartner.id = :partnerId")
    fun getPartnerLumpSumsTotal(@Param("partnerId") partnerId: Long): BigDecimal

}
