package io.cloudflight.jems.server.project.service.workpackage.activity.get_activity

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverableTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityTranslatedValue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class GetActivityTest {

    companion object {
        val activity1 = WorkPackageActivity(
            translatedValues = setOf(
                WorkPackageActivityTranslatedValue(language = SystemLanguage.EN, title = null, description = "en_desc"),
                WorkPackageActivityTranslatedValue(language = SystemLanguage.CS, title = "", description = null),
                WorkPackageActivityTranslatedValue(language = SystemLanguage.SK, title = "sk_title", description = "sk_desc"),
            ),
            startPeriod = 1,
            endPeriod = 3,
            deliverables = listOf(
                WorkPackageActivityDeliverable(
                    period = 1,
                    translatedValues = setOf(
                        WorkPackageActivityDeliverableTranslatedValue(language = SystemLanguage.EN, description = "en_deliv_desc"),
                        WorkPackageActivityDeliverableTranslatedValue(language = SystemLanguage.CS, description = null),
                    )
                )
            ),
        )
    }

    @MockK
    lateinit var persistence: WorkPackagePersistence

    @InjectMockKs
    lateinit var getActivity: GetActivity

    @Test
    fun getActivitiesForWorkPackage() {
        every { persistence.getWorkPackageActivitiesForWorkPackage(1L) } returns listOf(activity1)

        assertThat(getActivity.getActivitiesForWorkPackage(1L)).containsExactly(activity1)
    }

}
