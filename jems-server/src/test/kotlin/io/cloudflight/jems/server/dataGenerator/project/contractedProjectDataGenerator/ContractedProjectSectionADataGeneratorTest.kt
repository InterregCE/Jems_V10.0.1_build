package io.cloudflight.jems.server.dataGenerator.project.contractedProjectDataGenerator

import io.cloudflight.jems.api.project.ProjectApi
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_DURATION
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.PROJECT_DATA_INITIALIZER_ORDER
import io.cloudflight.jems.server.dataGenerator.project.FIRST_VERSION
import io.cloudflight.jems.server.dataGenerator.project.inputProjectData
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.boot.web.server.LocalServerPort


@Order(PROJECT_DATA_INITIALIZER_ORDER + 10)
class ContractedProjectSectionADataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

    private val projectApi =
        FeignTestClientFactory.createClientApi(ProjectApi::class.java, port, config)

    @Test
    @ExpectSelect(40)
    @ExpectInsert(10)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should fill project identification section`() {
        assertThat(
            projectApi.updateProjectForm(CONTRACTED_PROJECT_ID, inputProjectData(FIRST_VERSION, CONTRACTED_PROJECT_DURATION))
        ).isNotNull
    }
}
