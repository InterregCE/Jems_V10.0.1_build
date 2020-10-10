package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetExternal
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetExternalRepository : CrudRepository<ProjectPartnerBudgetExternal, Long> {

    fun findAllByPartnerIdOrderByIdAsc(partnerId: Long): List<ProjectPartnerBudgetExternal>

}
