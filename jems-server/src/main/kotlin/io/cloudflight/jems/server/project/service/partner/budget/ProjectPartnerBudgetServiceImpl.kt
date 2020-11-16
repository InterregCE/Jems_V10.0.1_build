package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputGeneralBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputStaffCostBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputTravelBudget
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.stream.Collectors

@Service
class ProjectPartnerBudgetServiceImpl(
    private val projectPartnerBudgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val projectPartnerBudgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val projectPartnerBudgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val projectPartnerBudgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val projectPartnerBudgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val getBudgetOptionsInteractor: GetBudgetOptionsInteractor
) : ProjectPartnerBudgetService {

    companion object {
        const val MAX_ALLOWED_AMOUNT = 300
        private val MAX_ALLOWED_VALUE = BigDecimal.valueOf(999_999_999_99L, 2)
    }

    //region StuffCosts

    @Transactional(readOnly = true)
    override fun getStaffCosts(partnerId: Long): List<InputStaffCostBudget> {
        return projectPartnerBudgetStaffCostRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toStaffCostOutput() }
    }

    @Transactional
    override fun updateStaffCosts(partnerId: Long, staffCosts: List<InputStaffCostBudget>): List<InputStaffCostBudget> {
        validateInput(staffCosts)

        val toBeRemoved = retrieveToBeRemoved(
            newData = staffCosts,
            old = projectPartnerBudgetStaffCostRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetStaffCostRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetStaffCostRepository
            .saveAll(staffCosts.map { it.toStaffCost(partnerId) })
            .map { it.toStaffCostOutput() }
    }
    //endregion StuffCosts

    //region Travel

    @Transactional(readOnly = true)
    override fun getTravel(partnerId: Long): List<InputTravelBudget> {
        return projectPartnerBudgetTravelRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toTravelOutput() }
    }

    @Transactional
    override fun updateTravel(partnerId: Long, travel: List<InputTravelBudget>): List<InputTravelBudget> {
        validateInput(travel)

        val toBeRemoved = retrieveToBeRemoved(
            newData = travel,
            old = projectPartnerBudgetTravelRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetTravelRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetTravelRepository
            .saveAll(travel.map { it.toTravel(partnerId) })
            .map { it.toTravelOutput() }
    }
    //endregion Travel

    //region External

    @Transactional(readOnly = true)
    override fun getExternal(partnerId: Long): List<InputGeneralBudget> {
        return projectPartnerBudgetExternalRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toExternalOutput() }
    }

    @Transactional
    override fun updateExternal(partnerId: Long, externals: List<InputGeneralBudget>): List<InputGeneralBudget> {
        validateInput(externals)

        val toBeRemoved = retrieveToBeRemoved(
            newData = externals,
            old = projectPartnerBudgetExternalRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetExternalRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetExternalRepository
            .saveAll(externals.map { it.toExternal(partnerId) })
            .map { it.toExternalOutput() }
    }
    //endregion External

    // region Equipment

    @Transactional(readOnly = true)
    override fun getEquipment(partnerId: Long): List<InputGeneralBudget> {
        return projectPartnerBudgetEquipmentRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toEquipmentOutput() }
    }

    @Transactional
    override fun updateEquipment(partnerId: Long, equipments: List<InputGeneralBudget>): List<InputGeneralBudget> {
        validateInput(equipments)

        val toBeRemoved = retrieveToBeRemoved(
            newData = equipments,
            old = projectPartnerBudgetEquipmentRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetEquipmentRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetEquipmentRepository
            .saveAll(equipments.map { it.toEquipment(partnerId) })
            .map { it.toEquipmentOutput() }
    }
    //endregion Equipment

    //region Infrastructure

    @Transactional(readOnly = true)
    override fun getInfrastructure(partnerId: Long): List<InputGeneralBudget> {
        return projectPartnerBudgetInfrastructureRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toInfrastructureOutput() }
    }

    @Transactional
    override fun updateInfrastructure(partnerId: Long, infrastructures: List<InputGeneralBudget>): List<InputGeneralBudget> {
        validateInput(infrastructures)

        val toBeRemoved = retrieveToBeRemoved(
            newData = infrastructures,
            old = projectPartnerBudgetInfrastructureRepository.findAllByPartnerIdOrderByIdAsc(partnerId)
        )

        projectPartnerBudgetInfrastructureRepository.deleteAll(toBeRemoved)
        return projectPartnerBudgetInfrastructureRepository
            .saveAll(infrastructures.map { it.toInfrastructure(partnerId) })
            .map { it.toInfrastructureOutput() }
    }
    //endregion Infrastructure


    @Transactional(readOnly = true)
    override fun getTotal(partnerId: Long): BigDecimal {
        val budgetOptions = getBudgetOptionsInteractor.getBudgetOptions(partnerId)
        return PartnerBudget(
            staffCostsFlatRate = budgetOptions?.staffCostsFlatRate,
            officeOnStaffFlatRate = budgetOptions?.officeAdministrationFlatRate,
            staffCosts = projectPartnerBudgetStaffCostRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO,
            travelCosts = projectPartnerBudgetTravelRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO,
            externalCosts = projectPartnerBudgetExternalRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO,
            equipmentCosts = projectPartnerBudgetEquipmentRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO,
            infrastructureCosts = projectPartnerBudgetInfrastructureRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO
        ).totalSum()
    }

    private fun validateInput(budgetList: List<InputBudget>) {

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
