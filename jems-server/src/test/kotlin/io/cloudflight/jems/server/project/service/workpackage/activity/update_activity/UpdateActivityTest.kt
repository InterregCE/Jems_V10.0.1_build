package io.cloudflight.jems.server.project.service.workpackage.activity.update_activity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.callDetail
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.ACTIVITY_DESCRIPTION_SIZE_ERROR_KEY
import io.cloudflight.jems.server.project.service.workpackage.activity.ACTIVITY_MAX_ERROR_KEY
import io.cloudflight.jems.server.project.service.workpackage.activity.ACTIVITY_START_PERIOD_LATE_ERROR_KEY
import io.cloudflight.jems.server.project.service.workpackage.activity.ACTIVITY_TITLE_SIZE_ERROR_KEY
import io.cloudflight.jems.server.project.service.workpackage.activity.DELIVERABLES_MAX_ERROR_KEY
import io.cloudflight.jems.server.project.service.workpackage.activity.DELIVERABLES_NOT_ENABLED_ERROR_KEY
import io.cloudflight.jems.server.project.service.workpackage.activity.DELIVERABLE_DESCRIPTION_LONG_ERROR_KEY
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Sort
import java.util.stream.Collectors

@ExtendWith(MockKExtension::class)
internal class UpdateActivityTest {

    companion object {
        const val projectId = 1L
        val callDetail = callDetail()

        val activity1 = WorkPackageActivity(
            workPackageId = 1L,
            title = setOf(
                InputTranslation(language = EN, translation = null),
                InputTranslation(language = CS, translation = ""),
                InputTranslation(language = SK, translation = "sk_title"),
            ),
            description = setOf(
                InputTranslation(language = EN, translation = "en_desc"),
                InputTranslation(language = CS, translation = null),
                InputTranslation(language = SK, translation = "sk_desc"),
            ),
            startPeriod = 1,
            endPeriod = 3,
            deliverables = listOf(
                WorkPackageActivityDeliverable(
                    period = 1,
                    description = setOf(InputTranslation(language = EN, translation = "en_deliv_desc"))
                )
            ),
            partnerIds = setOf(3)
        )

        val projectPartnerIds = listOf(
            ProjectPartnerSummary(id = 3, abbreviation = "lp1", role = ProjectPartnerRole.LEAD_PARTNER, active = true),
            ProjectPartnerSummary(id = 5, abbreviation = "p2", role = ProjectPartnerRole.PARTNER, active = true)
        )
    }

    @MockK
    lateinit var persistence: WorkPackagePersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var updateActivity: UpdateActivity

    @MockK
    lateinit var veryBigActivitiesList: List<WorkPackageActivity>
    @MockK
    lateinit var veryBigDeliverablesList: List<WorkPackageActivityDeliverable>

    @BeforeEach
    fun setup() {
        every { callPersistence.getCallByProjectId(projectId) } returns callDetail
    }

    @Test
    fun updateActivitiesForWorkPackage() {
        every { persistence.updateWorkPackageActivities(1L, any()) } returnsArgument 1
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.unsorted()) } returns projectPartnerIds
        assertThat(updateActivity.updateActivitiesForWorkPackage(projectId, 1L, listOf(activity1))).containsExactly(activity1)
    }

    @Test
    fun `update activities when max allowed activities amount reached`() {
        every { veryBigActivitiesList.size } returns 21
        val exception = assertThrows<I18nValidationException> { updateActivity.updateActivitiesForWorkPackage(projectId, 2L, veryBigActivitiesList) }
        assertThat(exception.i18nKey).isEqualTo(ACTIVITY_MAX_ERROR_KEY)
    }

    @Test
    fun `update activities - empty activities should pass`() {
        every { persistence.updateWorkPackageActivities(3L, any()) } returns emptyList()
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.unsorted()) } returns projectPartnerIds
        assertDoesNotThrow { updateActivity.updateActivitiesForWorkPackage(projectId, 3L, emptyList()) }
    }

    @Test
    fun `update activities - empty deliverables should pass`() {
        every { persistence.updateWorkPackageActivities(4L, any()) } returns emptyList()
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.unsorted()) } returns projectPartnerIds
        assertDoesNotThrow { updateActivity.updateActivitiesForWorkPackage(
            1L,
            4L,
            listOf(WorkPackageActivity(projectId, 4L, deliverables = emptyList()))
        ) }
    }

    @Test
    fun `update activities when max allowed deliverables amount reached`() {
        every { veryBigDeliverablesList.size } returns 21
        val toBeSaved = listOf(WorkPackageActivity(1L, 5L, deliverables = veryBigDeliverablesList))
        val exception = assertThrows<I18nValidationException> { updateActivity.updateActivitiesForWorkPackage(projectId, 5L, toBeSaved) }
        assertThat(exception.i18nKey).isEqualTo(DELIVERABLES_MAX_ERROR_KEY)
    }

    @Test
    fun `update activities when start period is after end period`() {
        val toBeSaved = listOf(WorkPackageActivity(1L, 6L, startPeriod = 2568, endPeriod = 2567))
        val exception = assertThrows<I18nValidationException> { updateActivity.updateActivitiesForWorkPackage(projectId, 6L, toBeSaved) }
        assertThat(exception.i18nKey).isEqualTo(ACTIVITY_START_PERIOD_LATE_ERROR_KEY)
    }

    @Test
    fun `update activities without partners assigned`() {
        every { persistence.updateWorkPackageActivities(1L, any()) } returnsArgument 1
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.unsorted()) } returns emptyList()
        val activity = activity1.copy(partnerIds = emptySet())
        assertThat(updateActivity.updateActivitiesForWorkPackage(projectId, 1L, listOf(activity)))
            .containsExactly(activity)
    }

    @Test
    fun `update activities when partner is not assigned to project`() {
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.unsorted()) } returns projectPartnerIds
        val exception = assertThrows<PartnersNotFound> {
            updateActivity.updateActivitiesForWorkPackage(projectId, 2L, listOf(activity1.copy(partnerIds = setOf(3, 10))))
        }
        assertThat(exception.message).isEqualTo("PartnerIds: 10")
    }

    @Test
    fun `update activities throwing error on adding disabled deliverables`() {
        val pId = 3L
        val callDetailWConfig = callDetail()
        every { callPersistence.getCallByProjectId(pId) } returns callDetailWConfig
        callDetailWConfig.applicationFormFieldConfigurations.add(
            ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_ACTIVITIES_DELIVERABLES.id, FieldVisibilityStatus.NONE)
        )
        val exception = assertThrows<I18nValidationException> {
            updateActivity.updateActivitiesForWorkPackage(pId, 2L, listOf(activity1))
        }
        assertThat(exception.i18nKey).isEqualTo(DELIVERABLES_NOT_ENABLED_ERROR_KEY)
    }

    @Test
    fun `update activities successful on not adding disabled deliverables`() {
        val pId = 4L
        val callDetailWConfig = callDetail()
        every { callPersistence.getCallByProjectId(pId) } returns callDetailWConfig
        callDetailWConfig.applicationFormFieldConfigurations.add(
            ApplicationFormFieldConfiguration(ApplicationFormFieldSetting.PROJECT_ACTIVITIES_DELIVERABLES.id, FieldVisibilityStatus.NONE)
        )
        every { partnerPersistence.findAllByProjectIdForDropdown(pId, Sort.unsorted()) } returns projectPartnerIds
        every { persistence.updateWorkPackageActivities(2L, any()) } returnsArgument 1

        assertDoesNotThrow {
            updateActivity.updateActivitiesForWorkPackage(pId, 2L, listOf(activity1.copy(deliverables = emptyList())))
        }
    }

    @Test
    fun `update activities when title is too long`() {
        val title = setOf(InputTranslation(
            language = CS,
            translation = getStringOfLength(201)
        ))
        val toBeSaved = listOf(WorkPackageActivity(1L, 7L, title = title))
        val exception = assertThrows<I18nValidationException> { updateActivity.updateActivitiesForWorkPackage(projectId, 7L, toBeSaved) }
        assertThat(exception.i18nKey).isEqualTo(ACTIVITY_TITLE_SIZE_ERROR_KEY)
    }

    @Test
    fun `update activities when description is too long`() {
        val description = setOf(InputTranslation(
            language = SK,
            translation = getStringOfLength(1001)
        ))
        val toBeSaved = listOf(WorkPackageActivity(1L, 8L, description = description))
        val exception = assertThrows<I18nValidationException> { updateActivity.updateActivitiesForWorkPackage(projectId, 8L, toBeSaved) }
        assertThat(exception.i18nKey).isEqualTo(ACTIVITY_DESCRIPTION_SIZE_ERROR_KEY)
    }

    @Test
    fun `update activity deliverables when description is too long`() {
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.unsorted()) } returns projectPartnerIds
        every { persistence.updateWorkPackageActivities(any(), any()) } returnsArgument 1
        val description = InputTranslation(
            language = EN,
            translation = getStringOfLength(1001)
        )
        val toBeSaved = listOf(WorkPackageActivity(1L, 9L, deliverables = listOf(WorkPackageActivityDeliverable(description = setOf(description)))))
        val exception = assertThrows<I18nValidationException> { updateActivity.updateActivitiesForWorkPackage(projectId, 9L, toBeSaved) }
        assertThat(exception.i18nKey).isEqualTo(DELIVERABLE_DESCRIPTION_LONG_ERROR_KEY)
    }

    private fun getStringOfLength(length: Int): String =
        IntArray(length).map { "x" }.stream().collect(Collectors.joining())

}
