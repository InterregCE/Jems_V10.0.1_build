package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructure
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetInfrastructureRepository : CrudRepository<ProjectPartnerBudgetInfrastructure, Long> {

    fun findAllByPartnerIdOrderByIdAsc(partnerId: Long): List<ProjectPartnerBudgetInfrastructure>

}
