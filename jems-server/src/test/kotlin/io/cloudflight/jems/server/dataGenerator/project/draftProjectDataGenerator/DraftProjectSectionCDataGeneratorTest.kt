package io.cloudflight.jems.server.dataGenerator.project.draftProjectDataGenerator

import io.cloudflight.jems.api.project.ProjectDescriptionApi
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.result.ProjectResultApi
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.DRAFT_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.PROJECT_DATA_INITIALIZER_ORDER
import io.cloudflight.jems.server.dataGenerator.project.FIRST_VERSION
import io.cloudflight.jems.server.dataGenerator.project.inputProjectLongTermPlans
import io.cloudflight.jems.server.dataGenerator.project.inputProjectManagement
import io.cloudflight.jems.server.dataGenerator.project.inputProjectOverallObjective
import io.cloudflight.jems.server.dataGenerator.project.inputProjectRelevance
import io.cloudflight.jems.server.dataGenerator.project.projectResultUpdateRequestDTO
import io.cloudflight.jems.server.dataGenerator.project.versionedInputTranslation
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.boot.web.server.LocalServerPort


@Order(PROJECT_DATA_INITIALIZER_ORDER + 30)
class DraftProjectSectionCDataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

    private val projectDescriptionApi =
        FeignTestClientFactory.createClientApi(ProjectDescriptionApi::class.java, port, config)
    private val resultApi =
        FeignTestClientFactory.createClientApi(ProjectResultApi::class.java, port, config)


    @Test
    @ExpectSelect(12)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should fill project overall objective section`() {
        assertThat(
            projectDescriptionApi.updateProjectOverallObjective(
                DRAFT_PROJECT_ID,
                inputProjectOverallObjective(FIRST_VERSION)
            )
        ).isNotNull
    }

    @Test
    @ExpectSelect(12)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should fill project relevance and context section`() {
        assertThat(
            projectDescriptionApi.updateProjectRelevance(DRAFT_PROJECT_ID, inputProjectRelevance(FIRST_VERSION))
        ).isNotNull
    }

    @Test
    @ExpectSelect(12)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should fill project partnership and context section`() {
        assertThat(
            projectDescriptionApi.updateProjectPartnership(
                DRAFT_PROJECT_ID,
                InputProjectPartnership(partnership = versionedInputTranslation("partnership", FIRST_VERSION))
            )
        ).isNotNull
    }

    @Test
    @ExpectSelect(16)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should fill project result section`() {
        assertThat(
            resultApi.updateProjectResults(DRAFT_PROJECT_ID, listOf(projectResultUpdateRequestDTO(FIRST_VERSION)))
        ).isNotNull
    }

    @Test
    @ExpectSelect(12)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should fill project management section`() {
        assertThat(
            projectDescriptionApi.updateProjectManagement(DRAFT_PROJECT_ID, inputProjectManagement(FIRST_VERSION))
        ).isNotNull
    }

    @Test
    @ExpectSelect(12)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should fill project long term plans section`() {
        assertThat(
            projectDescriptionApi.updateProjectLongTermPlans(
                DRAFT_PROJECT_ID,
                inputProjectLongTermPlans(FIRST_VERSION)
            )
        ).isNotNull
    }
}
