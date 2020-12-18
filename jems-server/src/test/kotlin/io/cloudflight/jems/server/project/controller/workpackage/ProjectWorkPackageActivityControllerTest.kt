package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.programme.dto.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDeliverableDTO
import io.cloudflight.jems.server.project.service.workpackage.activity.get_activity.GetActivityInteractor
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverableTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.activity.update_activity.UpdateActivityInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProjectWorkPackageActivityControllerTest {

    companion object {
        val activity1 = WorkPackageActivity(
            translatedValues = setOf(
                WorkPackageActivityTranslatedValue(language = EN, title = null, description = "en_desc"),
                WorkPackageActivityTranslatedValue(language = CS, title = "", description = null),
                WorkPackageActivityTranslatedValue(language = SK, title = "sk_title", description = "sk_desc"),
            ),
            startPeriod = 1,
            endPeriod = 3,
            deliverables = listOf(
                WorkPackageActivityDeliverable(
                    period = 1,
                    translatedValues = setOf(
                        WorkPackageActivityDeliverableTranslatedValue(language = EN, description = "en_deliv_desc"),
                        WorkPackageActivityDeliverableTranslatedValue(language = CS, description = null),
                    )
                )
            ),
        )
        val activity2 = WorkPackageActivity(
            translatedValues = emptySet(),
            startPeriod = 4,
            endPeriod = 6,
            deliverables = emptyList(),
        )

    }

    @MockK
    lateinit var getActivityInteractor: GetActivityInteractor

    @MockK
    lateinit var updateActivityInteractor: UpdateActivityInteractor

    @InjectMockKs
    private lateinit var controller: ProjectWorkPackageActivityController

    @Test
    fun getActivities() {
        every { getActivityInteractor.getActivitiesForWorkPackage(1L) } returns listOf(activity1, activity2)

        assertThat(controller.getActivities(1L)).containsExactly(
            WorkPackageActivityDTO(
                title = setOf(InputTranslation(SK, "sk_title")),
                startPeriod = 1,
                endPeriod = 3,
                description = setOf(InputTranslation(EN, "en_desc"), InputTranslation(SK, "sk_desc")),
                deliverables = listOf(
                    WorkPackageActivityDeliverableDTO(period = 1, description = setOf(InputTranslation(EN, "en_deliv_desc")))
                ),
            ),
            WorkPackageActivityDTO(
                title = emptySet(),
                startPeriod = 4,
                endPeriod = 6,
                description = emptySet(),
                deliverables = emptyList(),
            ),
        )
    }

    @Test
    fun updateActivities() {
        val activitiesSlot = slot<List<WorkPackageActivity>>()
        // we test retrieval login in getActivities test
        every { updateActivityInteractor.updateActivitiesForWorkPackage(1L, capture(activitiesSlot)) } returns emptyList()

        val activityDto1 = WorkPackageActivityDTO(
                title = setOf(InputTranslation(EN, null), InputTranslation(CS, ""), InputTranslation(SK, "sk_title")),
                startPeriod = 1,
                endPeriod = 2,
                description = setOf(InputTranslation(EN, "en_desc"), InputTranslation(CS, ""), InputTranslation(SK, "sk_desc")),
                deliverables = listOf(
                    WorkPackageActivityDeliverableDTO(period = 1, description = setOf(
                        InputTranslation(EN, "en_deliv_desc"),
                        InputTranslation(CS, ""),
                        InputTranslation(SK, null),
                    )),
                    WorkPackageActivityDeliverableDTO(period = 2)
                ),
        )
        val activityDto2 = WorkPackageActivityDTO(
                title = emptySet(),
                startPeriod = 3,
                endPeriod = 4,
                description = emptySet(),
                deliverables = emptyList(),
        )

        controller.updateActivities(1L, listOf(activityDto1, activityDto2))

        assertThat(activitiesSlot.captured).containsExactly(
            WorkPackageActivity(
                translatedValues = setOf(
                    WorkPackageActivityTranslatedValue(language = EN, title = null, description = "en_desc"),
                    WorkPackageActivityTranslatedValue(language = SK, title = "sk_title", description = "sk_desc"),
                ),
                startPeriod = 1,
                endPeriod = 2,
                deliverables = listOf(
                    WorkPackageActivityDeliverable(
                        period = 1,
                        translatedValues = setOf(
                            WorkPackageActivityDeliverableTranslatedValue(language = EN, description = "en_deliv_desc"),
                        )
                    ),
                    WorkPackageActivityDeliverable(period = 2)
                ),
            ),
            WorkPackageActivity(
                translatedValues = emptySet(),
                startPeriod = 3,
                endPeriod = 4,
                deliverables = emptyList(),
            )
        )
    }

}
