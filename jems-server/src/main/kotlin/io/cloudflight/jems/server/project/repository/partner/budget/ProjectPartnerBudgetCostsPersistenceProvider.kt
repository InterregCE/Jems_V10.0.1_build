package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.budget.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerBudgetCostsPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectPersistence: ProjectPersistence,
    private val budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val budgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val budgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository,
    private val budgetLumpSumRepository: ProjectLumpSumRepository
) : ProjectPartnerBudgetCostsPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetStaffCosts(partnerId: Long, version: Int?): List<BudgetStaffCostEntry> =
        projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(partnerId),
            currentVersionFetcher = {
                budgetStaffCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetStaffCostEntries()
            },
            previousVersionFetcher = { timestamp ->
                budgetStaffCostRepository.findAllByPartnerIdAsOfTimestamp(partnerId, timestamp)
                    .toBudgetStaffCostEntryList()
            }
        )


    @Transactional(readOnly = true)
    override fun getBudgetStaffCostTotal(partnerId: Long): BigDecimal =
        budgetStaffCostRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetTravelAndAccommodationCosts(partnerId: Long) =
        budgetTravelRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
            .toBudgetTravelAndAccommodationCostEntries()

    @Transactional(readOnly = true)
    override fun getBudgetTravelAndAccommodationCostTotal(partnerId: Long): BigDecimal =
        budgetTravelRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetInfrastructureAndWorksCosts(partnerId: Long) =
        budgetInfrastructureRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
            .infrastructureEntitiesToBudgetGeneralEntries()

    @Transactional(readOnly = true)
    override fun getBudgetUnitCosts(partnerId: Long): List<BudgetUnitCostEntry> =
        budgetUnitCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId).toModel()

    @Transactional(readOnly = true)
    override fun getBudgetUnitCostTotal(partnerId: Long): BigDecimal =
        budgetUnitCostRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetInfrastructureAndWorksCostTotal(partnerId: Long): BigDecimal =
        budgetInfrastructureRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetExternalExpertiseAndServicesCosts(partnerId: Long) =
        budgetExternalRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
            .externalEntitiesToBudgetGeneralEntries()

    @Transactional(readOnly = true)
    override fun getBudgetExternalExpertiseAndServicesCostTotal(partnerId: Long): BigDecimal =
        budgetExternalRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetEquipmentCosts(partnerId: Long) =
        budgetEquipmentRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
            .equipmentEntitiesToBudgetGeneralEntries()

    @Transactional(readOnly = true)
    override fun getBudgetEquipmentCostTotal(partnerId: Long): BigDecimal =
        budgetEquipmentRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetLumpSumsCostTotal(partnerId: Long): BigDecimal =
        budgetLumpSumRepository.getPartnerLumpSumsTotal(partnerId) ?: BigDecimal.ZERO

}
