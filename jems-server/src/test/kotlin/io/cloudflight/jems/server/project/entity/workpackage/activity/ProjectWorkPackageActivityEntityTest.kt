package io.cloudflight.jems.server.project.entity.workpackage.activity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableId
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectWorkPackageActivityEntityTest: UnitTest() {

    companion object {
        private fun deliverableId(workPackageId: Long, activityNumber: Int, deliverableNumber: Int) = WorkPackageActivityDeliverableId(
            activityId = WorkPackageActivityId(workPackageId = workPackageId, activityNumber = activityNumber),
            deliverableNumber = deliverableNumber
        )

        private val deliverable1 = WorkPackageActivityDeliverableEntity(
            deliverableId = deliverableId(workPackageId = 1L, activityNumber = 10, deliverableNumber = 100),
            translatedValues = setOf(
                WorkPackageActivityDeliverableTranslationEntity(
                    translationId = WorkPackageActivityDeliverableTranslationId(
                        deliverableId = deliverableId(workPackageId = 1L, activityNumber = 10, deliverableNumber = 100),
                        language = SystemLanguage.ES
                    ),
                    description = "ES description",
                )
            ),
            startPeriod = 1,
        )
        private val deliverable2 = WorkPackageActivityDeliverableEntity(
            deliverableId = deliverableId(workPackageId = 1L, activityNumber = 10, deliverableNumber = 100),
            translatedValues = setOf(
                WorkPackageActivityDeliverableTranslationEntity(
                    translationId = WorkPackageActivityDeliverableTranslationId(
                        deliverableId = deliverableId(workPackageId = 1L, activityNumber = 10, deliverableNumber = 100),
                        language = SystemLanguage.ES
                    ),
                    description = "ES description",
                )
            ),
            startPeriod = 1,
        )
    }

    @Test
    fun `WorkPackageActivityDeliverableEntity should be equal based on data`() {
        // preconditions:
        assertThat(deliverable1 !== deliverable2)
        assertThat(deliverable1.deliverableId !== deliverable2.deliverableId)
        // test equals and hashCode:
        assertThat(deliverable1 == deliverable2)
    }

    @Test
    fun `WorkPackageActivityDeliverableEntity should be equal when used in set`() {
        val deliverableDifferent = WorkPackageActivityDeliverableEntity(
            deliverableId = deliverableId(workPackageId = 1L, activityNumber = 10, deliverableNumber = 100),
            translatedValues = emptySet(),
            startPeriod = 922,
        )
        assertThat(deliverable1 != deliverableDifferent)
        assertThat(deliverable1.hashCode()).isEqualTo(deliverableDifferent.hashCode())
    }

    @Test
    fun `WorkPackageActivityEntity deliverables should be equal based on data`() {
        val activity = WorkPackageActivityEntity(
            activityId = WorkPackageActivityId(workPackageId = 546L, activityNumber = 256),
            deliverables = setOf(deliverable1, deliverable2)
        )
        assertThat(activity.deliverables).containsExactly(deliverable1)
        assertThat(activity.deliverables).containsExactly(deliverable2)
    }

}
