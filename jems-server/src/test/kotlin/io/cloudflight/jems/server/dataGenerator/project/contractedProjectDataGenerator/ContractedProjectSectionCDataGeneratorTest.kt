package io.cloudflight.jems.server.dataGenerator.project.contractedProjectDataGenerator

import io.cloudflight.jems.api.project.ProjectDescriptionApi
import io.cloudflight.jems.api.project.result.ProjectResultApi
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.PROJECT_DATA_INITIALIZER_ORDER
import io.cloudflight.jems.server.dataGenerator.project.FIRST_VERSION
import io.cloudflight.jems.server.dataGenerator.project.inputProjectLongTermPlans
import io.cloudflight.jems.server.dataGenerator.project.inputProjectManagement
import io.cloudflight.jems.server.dataGenerator.project.inputProjectOverallObjective
import io.cloudflight.jems.server.dataGenerator.project.inputProjectPartnership
import io.cloudflight.jems.server.dataGenerator.project.inputProjectRelevance
import io.cloudflight.jems.server.dataGenerator.project.projectResultUpdateRequestDTO
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
class ContractedProjectSectionCDataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

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
                CONTRACTED_PROJECT_ID,
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
            projectDescriptionApi.updateProjectRelevance(CONTRACTED_PROJECT_ID, inputProjectRelevance(FIRST_VERSION))
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
                CONTRACTED_PROJECT_ID, inputProjectPartnership(FIRST_VERSION)
            )
        ).isNotNull
    }

    @Test
    @ExpectSelect(17)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should fill project result section`() {
        assertThat(
            resultApi.updateProjectResults(CONTRACTED_PROJECT_ID, listOf(projectResultUpdateRequestDTO(FIRST_VERSION)))
        ).isNotNull
    }

    @Test
    @ExpectSelect(12)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should fill project management section`() {
        assertThat(
            projectDescriptionApi.updateProjectManagement(CONTRACTED_PROJECT_ID, inputProjectManagement(FIRST_VERSION))
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
                CONTRACTED_PROJECT_ID,
                inputProjectLongTermPlans(FIRST_VERSION)
            )
        ).isNotNull
    }
}
