package io.cloudflight.jems.server.project.service.report.partner.contribution.updateProjectPartnerReportContribution

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionData
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionCustom
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionExisting
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionWrapper
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.toModelData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID
import kotlin.collections.HashSet

@Service
class UpdateProjectPartnerReportContribution(
    private val reportContributionPersistence: ProjectReportContributionPersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateProjectPartnerReportContributionInteractor {

    companion object {
        private val MAX_NUMBER = BigDecimal.valueOf(999_999_999_99, 2)
        private val MIN_NUMBER = BigDecimal.ZERO

        private const val MAX_AMOUNT_OF_CONTRIBUTIONS = 25
    }

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportContributionException::class)
    override fun update(
        partnerId: Long,
        reportId: Long,
        data: UpdateProjectPartnerReportContributionWrapper
    ): ProjectPartnerReportContributionData {
        validateInputFields(data)
        val existingContributions = reportContributionPersistence.getPartnerReportContribution(partnerId, reportId = reportId)

        // filter only existing and deletable
        val toBeDeletedIds = data.toBeDeletedIds intersect
            existingContributions.filter { it.createdInThisReport }.mapTo(HashSet()) { it.id }

        validateMaxAmountOfContributions(
            existing = existingContributions.size,
            removed = toBeDeletedIds.size,
            added = data.toBeCreated.size,
        )

        val existingById = existingContributions.associateBy { it.id }.filterKeys { !toBeDeletedIds.contains(it) }

        reportContributionPersistence.deleteByIds(toBeDeletedIds)
        reportContributionPersistence.updateExisting(toBeUpdated = data.toBeUpdated.prepareOnlyAllowedChanges(existingById))
        reportContributionPersistence.addNew(reportId = reportId, toBeCreated = data.toBeCreated.toCreateModels())

        return reportContributionPersistence
            .getPartnerReportContribution(partnerId, reportId = reportId)
            .toModelData()
    }

    private fun validateInputFields(data: UpdateProjectPartnerReportContributionWrapper) {
        generalValidator.throwIfAnyIsInvalid(
            *data.toBeCreated.mapIndexed { index, it ->
                generalValidator.maxLength(it.sourceOfContribution, 255, "new.sourceOfContribution[$index]")
            }.toTypedArray(),
            *data.toBeCreated.mapIndexed { index, it ->
                generalValidator.numberBetween(it.currentlyReported, MIN_NUMBER, MAX_NUMBER, "new.currentlyReported[$index]")
            }.toTypedArray(),
            *data.toBeUpdated.mapIndexed { index, it ->
                generalValidator.maxLength(it.sourceOfContribution, 255, "sourceOfContribution[$index]")
            }.toTypedArray(),
            *data.toBeUpdated.mapIndexed { index, it ->
                generalValidator.numberBetween(it.currentlyReported, MIN_NUMBER, MAX_NUMBER, "currentlyReported[$index]")
            }.toTypedArray(),
        )
    }

    private fun validateMaxAmountOfContributions(existing: Int, removed: Int, added: Int) {
        if (existing - removed + added > MAX_AMOUNT_OF_CONTRIBUTIONS)
            throw MaxAmountOfContributionsReachedException(MAX_AMOUNT_OF_CONTRIBUTIONS)
    }

    private fun List<UpdateProjectPartnerReportContributionCustom>.toCreateModels() = map {
        CreateProjectPartnerReportContribution(
            sourceOfContribution = it.sourceOfContribution,
            legalStatus = it.legalStatus,
            idFromApplicationForm = null,
            historyIdentifier = UUID.randomUUID(),
            createdInThisReport = true,
            amount = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = it.currentlyReported,
        )
    }

    private fun Set<UpdateProjectPartnerReportContributionExisting>.prepareOnlyAllowedChanges(
        existingById: Map<Long, ProjectPartnerReportEntityContribution>
    ) = filter { existingById.keys.contains(it.id) }.map { new ->
        with(existingById[new.id]!!) {
            UpdateProjectPartnerReportContributionExisting(
                id = new.id,
                currentlyReported = new.currentlyReported,
                sourceOfContribution = if (createdInThisReport) new.sourceOfContribution else this.sourceOfContribution,
                legalStatus = if (createdInThisReport) new.legalStatus else this.legalStatus,
            )
        }
    }

}
