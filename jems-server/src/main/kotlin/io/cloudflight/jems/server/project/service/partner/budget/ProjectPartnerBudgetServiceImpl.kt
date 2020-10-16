package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.server.exception.I18nValidationException
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.stream.Collectors

@Service
class ProjectPartnerBudgetServiceImpl(
        private val projectPartnerRepository: ProjectPartnerRepository,
        private val projectPartnerBudgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
        private val projectPartnerBudgetTravelRepository: ProjectPartnerBudgetTravelRepository,
        private val projectPartnerBudgetExternalRepository: ProjectPartnerBudgetExternalRepository,
        private val projectPartnerBudgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
        private val projectPartnerBudgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository
) : ProjectPartnerBudgetService {

    companion object {
        const val MAX_ALLOWED_AMOUNT = 300
        private val MAX_ALLOWED_VALUE = BigDecimal.valueOf(999_999_999_999_999_99L, 2)
    }

    //region StuffCosts

    @Transactional(readOnly = true)
    override fun getStaffCosts(projectId: Long, partnerId: Long): List<InputBudget> {
        validateInput(projectId, partnerId)

        return projectPartnerBudgetStaffCostRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateStaffCosts(projectId: Long, partnerId: Long, staffCosts: List<InputBudget>): List<InputBudget> {
        validateInput(projectId, partnerId, staffCosts)

        val toBeRemoved = retrieveToBeRemoved(
            newData = staffCosts,
            old = projectPartnerBudgetStaffCostRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetStaffCostRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetStaffCostRepository
            .saveAll(staffCosts.map { it.toStaffCost(partnerId) })
            .map { it.toOutput() }
    }
    //endregion StuffCosts

    //region Travel

    @Transactional(readOnly = true)
    override fun getTravel(projectId: Long, partnerId: Long): List<InputBudget> {
        validateInput(projectId, partnerId)

        return projectPartnerBudgetTravelRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateTravel(projectId: Long, partnerId: Long, travel: List<InputBudget>): List<InputBudget> {
        validateInput(projectId, partnerId, travel)

        val toBeRemoved = retrieveToBeRemoved(
            newData = travel,
            old = projectPartnerBudgetTravelRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetTravelRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetTravelRepository
            .saveAll(travel.map { it.toTravel(partnerId) })
            .map { it.toOutput() }
    }
    //endregion Travel

    //region External

    @Transactional(readOnly = true)
    override fun getExternal(projectId: Long, partnerId: Long): List<InputBudget> {
        validateInput(projectId, partnerId)

        return projectPartnerBudgetExternalRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateExternal(projectId: Long, partnerId: Long, externals: List<InputBudget>): List<InputBudget> {
        validateInput(projectId, partnerId, externals)

        val toBeRemoved = retrieveToBeRemoved(
            newData = externals,
            old = projectPartnerBudgetExternalRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetExternalRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetExternalRepository
            .saveAll(externals.map { it.toExternal(partnerId) })
            .map { it.toOutput() }
    }
    //endregion External

    // region Equipment

    @Transactional(readOnly = true)
    override fun getEquipment(projectId: Long, partnerId: Long): List<InputBudget> {
        validateInput(projectId, partnerId)

        return projectPartnerBudgetEquipmentRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateEquipment(projectId: Long, partnerId: Long, equipments: List<InputBudget>): List<InputBudget> {
        validateInput(projectId, partnerId, equipments)

        val toBeRemoved = retrieveToBeRemoved(
            newData = equipments,
            old = projectPartnerBudgetEquipmentRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetEquipmentRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetEquipmentRepository
            .saveAll(equipments.map { it.toEquipment(partnerId) })
            .map { it.toOutput() }
    }
    //endregion Equipment

    //region Infrastructure

    @Transactional(readOnly = true)
    override fun getInfrastructure(projectId: Long, partnerId: Long): List<InputBudget> {
        validateInput(projectId, partnerId)

        return projectPartnerBudgetInfrastructureRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateInfrastructure(projectId: Long, partnerId: Long, infrastructures: List<InputBudget>): List<InputBudget> {
        validateInput(projectId, partnerId, infrastructures)

        val toBeRemoved = retrieveToBeRemoved(
            newData = infrastructures,
            old = projectPartnerBudgetInfrastructureRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetInfrastructureRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetInfrastructureRepository
            .saveAll(infrastructures.map { it.toInfrastructure(partnerId) })
            .map { it.toOutput() }
    }
    //endregion Infrastructure

    private fun validateInput(projectId: Long, partnerId: Long, budgetList: List<InputBudget> = emptyList()) {
        if (projectPartnerRepository.findFirstByProjectIdAndId(projectId, partnerId).isEmpty)
            throw ResourceNotFoundException("projectPartner")

        if (budgetList.size > MAX_ALLOWED_AMOUNT)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.budget.max.allowed.reached"
            )

        if (!budgetList.parallelStream().allMatch {
                it.numberOfUnits <= MAX_ALLOWED_VALUE && it.pricePerUnit <= MAX_ALLOWED_VALUE
                    && it.numberOfUnits.multiply(it.pricePerUnit) <= MAX_ALLOWED_VALUE
            })
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.budget.number.out.of.range"
            )
    }

    private fun <T : CommonBudget> retrieveToBeRemoved(newData: List<InputBudget>, old: List<T>): Set<T> {
        val toBeUpdatedIds = newData.mapTo(HashSet()) { it.id }
        return old.stream()
            .filter { !toBeUpdatedIds.contains(it.id) }
            .collect(Collectors.toSet())
    }

}
