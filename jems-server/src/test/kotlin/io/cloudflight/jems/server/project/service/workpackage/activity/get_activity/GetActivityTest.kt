package io.cloudflight.jems.server.project.service.workpackage.activity.get_activity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetActivityTest : UnitTest() {

    private val activity1 = WorkPackageActivity(
        workPackageId = 1L,
        title = setOf(
            InputTranslation(language = SystemLanguage.EN, translation = null),
            InputTranslation(language = SystemLanguage.CS, translation = ""),
            InputTranslation(language = SystemLanguage.SK, translation = "sk_title"),
        ),
        description = setOf(
            InputTranslation(language = SystemLanguage.EN, translation = "en_desc"),
            InputTranslation(language = SystemLanguage.CS, translation = null),
            InputTranslation(language = SystemLanguage.SK, translation = "sk_desc"),
        ),
        startPeriod = 1,
        endPeriod = 3,
        deliverables = listOf(
            WorkPackageActivityDeliverable(
                period = 1,
                description = setOf(
                    InputTranslation(
                        language = SystemLanguage.EN,
                        translation = "en_deliv_desc"
                    ),
                    InputTranslation(language = SystemLanguage.CS, translation = null),
                )
            )
        ),
    )

    @MockK
    lateinit var persistence: WorkPackagePersistence

    @InjectMockKs
    lateinit var getActivity: GetActivity

    @Test
    fun getActivitiesForWorkPackage() {
        every { persistence.getWorkPackageActivitiesForWorkPackage(1L, 1L) } returns listOf(activity1)

        assertThat(getActivity.getActivitiesForWorkPackage(1L, 1L)).containsExactly(activity1)
    }

}
