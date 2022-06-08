package io.cloudflight.jems.server.dataGenerator.project.contractedProjectDataGenerator

import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_VERSIONS
import io.cloudflight.jems.server.dataGenerator.PROJECT_DATA_INITIALIZER_ORDER
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import io.cloudflight.jems.server.project.service.get_project_versions.GetProjectVersionsInteractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.beans.factory.annotation.Autowired


@Order(PROJECT_DATA_INITIALIZER_ORDER + 60)
class FetchContractedProjectCurrentDataGeneratorTest : DataGeneratorTest() {

    @Autowired
    private lateinit var getProject: GetProjectInteractor

    @Autowired
    private lateinit var getProjectVersions: GetProjectVersionsInteractor

    @Test
    @ExpectSelect(1)
    @ExpectInsert(0)
    @ExpectUpdate(0)
    @ExpectDelete(0)
    fun `should fetch project version data`() {
        CONTRACTED_PROJECT_VERSIONS = getProjectVersions.getProjectVersions(CONTRACTED_PROJECT_ID)
    }

    @Test
    @ExpectSelect(24)
    @ExpectInsert(0)
    @ExpectUpdate(0)
    @ExpectDelete(0)
    fun `should return project data`() {
        CONTRACTED_PROJECT = getProject.getProjectDetail(CONTRACTED_PROJECT_ID)
        assertThat(CONTRACTED_PROJECT).isNotNull
    }

}
