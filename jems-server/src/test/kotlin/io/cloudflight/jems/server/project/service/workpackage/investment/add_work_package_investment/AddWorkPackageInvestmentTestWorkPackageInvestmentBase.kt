package io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment

import io.cloudflight.jems.server.call.callDetail
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.workpackage.investment.UnitTestWorkPackageInvestmentBase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class AddWorkPackageInvestmentTestWorkPackageInvestmentBase : UnitTestWorkPackageInvestmentBase() {

    companion object {
        const val projectId = 1L

        private fun getListOfInvestmentAFConfig(allHidden: Boolean = true): MutableSet<ApplicationFormFieldConfiguration> {
            val titleVisibility = if (!allHidden) {
                FieldVisibilityStatus.STEP_ONE_AND_TWO
            } else {
                FieldVisibilityStatus.NONE
            }
            return mutableSetOf(
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_TITLE.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_PERIOD.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(
                    ApplicationFormFieldSetting.PROJECT_INVESTMENT_CROSS_BORDER_TRANSNATIONAL_RELEVANCE_OF_INVESTMENT.id,
                    FieldVisibilityStatus.NONE
                ),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_WHY_IS_INVESTMENT_NEEDED.id, titleVisibility),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_WHO_IS_BENEFITING.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_PILOT_CLARIFICATION.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_COUNTRY.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_STREET.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_HOUSE_NUMBER.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_POSTAL_CODE.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_CITY.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_RISK.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_DOCUMENTATION.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_DOCUMENTATION_EXPECTED_IMPACTS.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_WHO_OWNS_THE_INVESTMENT_SITE.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_OWNERSHIP_AFTER_END_OF_PROJECT.id, FieldVisibilityStatus.NONE),
                ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_INVESTMENT_MAINTENANCE.id, FieldVisibilityStatus.NONE)
            )
        }
        val callDetail = callDetail(
            applicationFormFieldConfigurations = getListOfInvestmentAFConfig(false)
        )
    }

    @MockK
    lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var addWorkPackageInvestment: AddWorkPackageInvestment

    @BeforeEach
    fun setup() {
        every { callPersistence.getCallByProjectId(projectId) } returns callDetail
    }

    @Test
    fun `should add a workPackageInvestment to the specified workPackage and return the UUID of newly created workPackageInvestment when workPackage already exists`() {
        every { persistence.countWorkPackageInvestments(workPackageId) } returns 5
        val workPackageInvestment = createWorkPackageInvestment(workPackageInvestmentId)
        every { persistence.addWorkPackageInvestment(workPackageId, any()) } returns workPackageInvestmentId

        val createdWorkPackageInvestmentId = addWorkPackageInvestment.addWorkPackageInvestment(projectId, workPackageId, workPackageInvestment)
        assertEquals(workPackageInvestmentId, createdWorkPackageInvestmentId)

        verify { callPersistence.getCallByProjectId(projectId) }
        verify { persistence.countWorkPackageInvestments(workPackageId) }
        verify { persistence.addWorkPackageInvestment(workPackageId, any()) }
        confirmVerified(persistence)
    }

    @Test
    fun `addWorkPackageInvestment should fail if there are already a lot of investments`() {
        every { persistence.countWorkPackageInvestments(workPackageId) } returns 20
        val workPackageInvestment = createWorkPackageInvestment(workPackageInvestmentId)

        val ex = assertThrows<I18nValidationException> { addWorkPackageInvestment.addWorkPackageInvestment(projectId, workPackageId, workPackageInvestment) }
        assertThat(ex.i18nKey).isEqualTo(INVESTMENTS_MAX_ERROR_KEY)

        verify { callPersistence.getCallByProjectId(projectId) }
        verify { persistence.countWorkPackageInvestments(workPackageId) }
        confirmVerified(persistence)
    }

    @Test
    fun `addWorkPackageInvestment should succeed if only one field is enabled on configuration`() {
        val workPackageInvestment = createWorkPackageInvestment(workPackageInvestmentId)
        val pId = 3L
        val callDetailWConfig = callDetail()
        callDetailWConfig.applicationFormFieldConfigurations.addAll(getListOfInvestmentAFConfig(false))
        every { callPersistence.getCallByProjectId(pId) } returns callDetailWConfig
        every { persistence.countWorkPackageInvestments(workPackageId) } returns 1

        assertDoesNotThrow {
            addWorkPackageInvestment.addWorkPackageInvestment(pId, workPackageId, workPackageInvestment)
        }
    }

    @Test
    fun `addWorkPackageInvestment should fail if investments are disabled on configuration`() {
        val workPackageInvestment = createWorkPackageInvestment(workPackageInvestmentId)
        val pId = 4L
        val callDetailWConfig = callDetail()
        callDetailWConfig.applicationFormFieldConfigurations.addAll(getListOfInvestmentAFConfig())
        every { callPersistence.getCallByProjectId(pId) } returns callDetailWConfig

        val ex = assertThrows<I18nValidationException> {
            addWorkPackageInvestment.addWorkPackageInvestment(pId, workPackageId, workPackageInvestment)
        }
        assertThat(ex.i18nKey).isEqualTo(INVESTMENTS_NOT_ENABLED_ERROR_KEY)
    }
}
