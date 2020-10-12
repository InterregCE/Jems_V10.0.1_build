package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface ProjectPartnerBudgetCommonRepository<T : CommonBudget> : CrudRepository<T, Long> {

    fun findAllByPartnerIdOrderByIdAsc(partnerId: Long): List<T>

}
