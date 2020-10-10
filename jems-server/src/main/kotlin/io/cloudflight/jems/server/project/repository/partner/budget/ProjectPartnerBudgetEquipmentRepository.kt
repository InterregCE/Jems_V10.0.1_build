package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEquipment
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetEquipmentRepository : CrudRepository<ProjectPartnerBudgetEquipment, Long> {

    fun findAllByPartnerIdOrderByIdAsc(partnerId: Long): List<ProjectPartnerBudgetEquipment>

}
