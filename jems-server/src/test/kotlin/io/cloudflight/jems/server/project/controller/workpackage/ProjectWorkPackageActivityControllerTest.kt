package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDeliverableDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.workpackage.activity.get_activity.GetActivityInteractor
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.update_activity.UpdateActivityInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectWorkPackageActivityControllerTest : UnitTest() {

    private val activity1 = WorkPackageActivity(
        workPackageId = 1L,
        activityNumber = 1,
        title = setOf(
            InputTranslation(language = SK, translation = "sk_title"),
        ),
        description = setOf(
            InputTranslation(language = EN, translation = "en_desc"),
            InputTranslation(language = SK, translation = "sk_desc"),
        ),
        startPeriod = 1,
        endPeriod = 3,
        deliverables = listOf(
            WorkPackageActivityDeliverable(
                deliverableNumber = 1,
                period = 1,
                description = setOf(
                    InputTranslation(language = EN, translation = "en_deliv_desc")
                )
            )
        ),
        partnerIds = setOf(2)
    )
    private val activity2 = WorkPackageActivity(
        workPackageId = 1L,
        activityNumber = 2,
        title = emptySet(),
        description = emptySet(),
        startPeriod = 4,
        endPeriod = 6,
        deliverables = emptyList(),
    )

    @MockK
    lateinit var getActivityInteractor: GetActivityInteractor

    @MockK
    lateinit var updateActivityInteractor: UpdateActivityInteractor

    @InjectMockKs
    private lateinit var controller: ProjectWorkPackageActivityController

    @Test
    fun getActivities() {
        every { getActivityInteractor.getActivitiesForWorkPackage(1L, 1L) } returns listOf(activity1, activity2)

        assertThat(controller.getActivities(1L, 1L)).containsExactly(
            WorkPackageActivityDTO(
                workPackageId = 1L,
                activityNumber = 1,
                title = setOf(InputTranslation(SK, "sk_title")),
                startPeriod = 1,
                endPeriod = 3,
                description = setOf(
                    InputTranslation(EN, "en_desc"), InputTranslation(SK, "sk_desc")
                ),
                deliverables = listOf(
                    WorkPackageActivityDeliverableDTO(
                        deliverableNumber = 1,
                        period = 1,
                        description = setOf(InputTranslation(EN, "en_deliv_desc"))
                    )
                ),
                partnerIds = setOf(2)
            ),
            WorkPackageActivityDTO(
                workPackageId = 1L,
                activityNumber = 2,
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
        every {
            updateActivityInteractor.updateActivitiesForWorkPackage(
                1L,
                1L,
                capture(activitiesSlot)
            )
        } returns emptyList()

        val activityDto1 = WorkPackageActivityDTO(
            workPackageId = 1L,
            title = setOf(InputTranslation(SK, "sk_title")),
            startPeriod = 1,
            endPeriod = 2,
            description = setOf(
                InputTranslation(EN, "en_desc"),
                InputTranslation(SK, "sk_desc")
            ),
            deliverables = listOf(
                WorkPackageActivityDeliverableDTO(
                    period = 1, description = setOf(
                        InputTranslation(EN, "en_deliv_desc"),
                    )
                ),
                WorkPackageActivityDeliverableDTO(period = 2)
            ),
            partnerIds = setOf(2, 3)
        )
        val activityDto2 = WorkPackageActivityDTO(
            workPackageId = 1L,
            title = emptySet(),
            startPeriod = 3,
            endPeriod = 4,
            description = emptySet(),
            deliverables = emptyList()
        )

        controller.updateActivities(1L, 1L, listOf(activityDto1, activityDto2))

        assertThat(activitiesSlot.captured).containsExactly(
            WorkPackageActivity(
                workPackageId = 1L,
                title = setOf(
                    InputTranslation(language = SK, translation = "sk_title"),
                ),
                description = setOf(
                    InputTranslation(language = EN, translation = "en_desc"),
                    InputTranslation(language = SK, translation = "sk_desc"),
                ),
                startPeriod = 1,
                endPeriod = 2,
                deliverables = listOf(
                    WorkPackageActivityDeliverable(
                        period = 1,
                        description = setOf(
                            InputTranslation(language = EN, translation = "en_deliv_desc"),
                        )
                    ),
                    WorkPackageActivityDeliverable(period = 2)
                ),
                partnerIds = setOf(2, 3)
            ),
            WorkPackageActivity(
                workPackageId = 1L,
                title = emptySet(),
                description = emptySet(),
                startPeriod = 3,
                endPeriod = 4,
                deliverables = emptyList(),
                partnerIds = emptySet()
            )
        )
    }

}
