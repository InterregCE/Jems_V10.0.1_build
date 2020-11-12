package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
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
        private val MAX_ALLOWED_VALUE = BigDecimal.valueOf(999_999_999_999_999_99L, 2)
    }

    //region StuffCosts

    @Transactional(readOnly = true)
    override fun getStaffCosts(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetStaffCostRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateStaffCosts(partnerId: Long, staffCosts: List<InputBudget>): List<InputBudget> {
        validateInput(staffCosts)

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
    override fun getTravel(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetTravelRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateTravel(partnerId: Long, travel: List<InputBudget>): List<InputBudget> {
        validateInput(travel)

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
    override fun getExternal(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetExternalRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateExternal(partnerId: Long, externals: List<InputBudget>): List<InputBudget> {
        validateInput(externals)

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
    override fun getEquipment(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetEquipmentRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateEquipment(partnerId: Long, equipments: List<InputBudget>): List<InputBudget> {
        validateInput(equipments)

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
    override fun getInfrastructure(partnerId: Long): List<InputBudget> {
        return projectPartnerBudgetInfrastructureRepository
            .findAllByPartnerIdOrderByIdAsc(partnerId)
            .map { it.toOutput() }
    }

    @Transactional
    override fun updateInfrastructure(partnerId: Long, infrastructures: List<InputBudget>): List<InputBudget> {
        validateInput(infrastructures)

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
