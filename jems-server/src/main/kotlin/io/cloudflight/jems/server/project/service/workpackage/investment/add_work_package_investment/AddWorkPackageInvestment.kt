package io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

const val INVESTMENTS_MAX_ERROR_KEY = "project.workPackage.investment.max.allowed.reached"
const val INVESTMENTS_NOT_ENABLED_ERROR_KEY = "project.workPackage.investments.not.enabled"

@Service
class AddWorkPackageInvestment(
    private val workPackagePersistence: WorkPackagePersistence,
    private val callPersistence: CallPersistence
) : AddWorkPackageInvestmentInteractor {

    companion object {
        private const val MAX_INVESTMENT_PER_WORK_PACKAGE = 20L
    }

    @CanUpdateProjectWorkPackage
    @Transactional
    override fun addWorkPackageInvestment(projectId: Long, workPackageId: Long, workPackageInvestment: WorkPackageInvestment): Long {
        val afConfig = callPersistence.getCallByProjectId(projectId).applicationFormFieldConfigurations
        validateWorkPackageConfiguration(afConfig)
        validateInvestmentsMaxCount(workPackageId = workPackageId)

        return workPackagePersistence.addWorkPackageInvestment(workPackageId, workPackageInvestment)
    }

    private fun validateWorkPackageConfiguration(afConfig: Set<ApplicationFormFieldConfiguration>) {
        val investmentEntries = afConfig.filter { allInvestmentConfigEntries().contains(it.id) }
        if (investmentEntries.all { it.visibilityStatus == FieldVisibilityStatus.NONE }) {
            throw I18nValidationException(i18nKey = INVESTMENTS_NOT_ENABLED_ERROR_KEY)
        }
    }

    private fun allInvestmentConfigEntries(): List<String> =
        listOf(
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_TITLE.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_PERIOD.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_CROSS_BORDER_TRANSNATIONAL_RELEVANCE_OF_INVESTMENT.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_WHO_IS_BENEFITING.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_PILOT_CLARIFICATION.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_COUNTRY.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_STREET.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_HOUSE_NUMBER.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_POSTAL_CODE.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_CITY.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_RISK.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_DOCUMENTATION.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_DOCUMENTATION_EXPECTED_IMPACTS.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_WHO_OWNS_THE_INVESTMENT_SITE.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_OWNERSHIP_AFTER_END_OF_PROJECT.id,
            ApplicationFormFieldSetting.PROJECT_INVESTMENT_MAINTENANCE.id
        )

    private fun validateInvestmentsMaxCount(workPackageId: Long) {
        if (workPackagePersistence.countWorkPackageInvestments(workPackageId) >= MAX_INVESTMENT_PER_WORK_PACKAGE)
            throw I18nValidationException(i18nKey = INVESTMENTS_MAX_ERROR_KEY)
    }
}
