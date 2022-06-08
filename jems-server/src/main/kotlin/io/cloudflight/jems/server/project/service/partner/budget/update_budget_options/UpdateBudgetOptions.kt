package io.cloudflight.jems.server.project.service.partner.budget.update_budget_options

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Service
class UpdateBudgetOptions(
    private val persistence: ProjectPartnerBudgetOptionsPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val partnerPersistence: PartnerPersistence
) : UpdateBudgetOptionsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    @ExceptionWrapper(UpdateBudgetOptionsException::class)
    override fun updateBudgetOptions(partnerId: Long, options: ProjectPartnerBudgetOptions) {

        validateFlatRatesCombinations(options)

        val callFlatRateSetup = persistence.getProjectCallFlatRateByPartnerId(partnerId)

        validateFlatRates(
            callFlatRateSetup = callFlatRateSetup,
            options = options,
        )

        validateContractedChanges(options, partnerId)

        if (options.isEmpty())
            return persistence.deleteBudgetOptions(partnerId)

        if (options.otherCostsOnStaffCostsFlatRate != null) {
            persistence.deleteTravelAndAccommodationCosts(partnerId)
            persistence.deleteEquipmentCosts(partnerId)
            persistence.deleteExternalCosts(partnerId)
            persistence.deleteInfrastructureCosts(partnerId)
            persistence.deleteUnitCosts(partnerId)
        }
        if (options.staffCostsFlatRate != null)
            persistence.deleteStaffCosts(partnerId)

        if (options.travelAndAccommodationOnStaffCostsFlatRate != null)
            persistence.deleteTravelAndAccommodationCosts(partnerId)

        persistence.updateBudgetOptions(partnerId, options)
    }

    private fun validateContractedChanges(inputOptions: ProjectPartnerBudgetOptions, partnerId: Long) {
        val versions = projectVersionPersistence
            .getAllVersionsByProjectId(partnerPersistence.getProjectIdForPartnerId(partnerId))

        if (versions.isEmpty() || !versions[0].status.isAlreadyContracted())
            return

        val partnerCreatedAt = ZonedDateTime.of(partnerPersistence.getById(partnerId).createdAt.toLocalDateTime(), ZoneOffset.UTC)
        if (partnerCreatedAt.isAfter(versions[0].createdAt))
            return

        if (!optionsEnabledOrDisabled(persistence.getBudgetOptions(partnerId), inputOptions))
            throw UpdateBudgetOptionsWhenProjectContracted()
    }

    private fun optionsEnabledOrDisabled(
        savedOptions: ProjectPartnerBudgetOptions?,
        inputOptions: ProjectPartnerBudgetOptions
    ) = (bothNullOrPresent(savedOptions?.officeAndAdministrationOnStaffCostsFlatRate, inputOptions.officeAndAdministrationOnStaffCostsFlatRate)
        && bothNullOrPresent(savedOptions?.officeAndAdministrationOnDirectCostsFlatRate, inputOptions.officeAndAdministrationOnDirectCostsFlatRate)
        && bothNullOrPresent(savedOptions?.otherCostsOnStaffCostsFlatRate, inputOptions.otherCostsOnStaffCostsFlatRate)
        && bothNullOrPresent(savedOptions?.staffCostsFlatRate, inputOptions.staffCostsFlatRate)
        && bothNullOrPresent(savedOptions?.travelAndAccommodationOnStaffCostsFlatRate, inputOptions.travelAndAccommodationOnStaffCostsFlatRate))

    private fun bothNullOrPresent(first: Int?, second: Int?): Boolean {
        return first == null && second == null || first != null && second != null
    }

}
