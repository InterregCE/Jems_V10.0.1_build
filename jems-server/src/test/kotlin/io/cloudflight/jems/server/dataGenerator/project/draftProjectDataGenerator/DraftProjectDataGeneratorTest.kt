package io.cloudflight.jems.server.dataGenerator.project.draftProjectDataGenerator

import io.cloudflight.jems.api.project.ProjectApi
import io.cloudflight.jems.api.project.dto.ProjectCreateDTO
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.DRAFT_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.PROJECT_DATA_INITIALIZER_ORDER
import io.cloudflight.jems.server.dataGenerator.STANDARD_CALL_DETAIL
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.boot.web.server.LocalServerPort


@Order(PROJECT_DATA_INITIALIZER_ORDER)
class DraftProjectDataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

    private val projectApi =
        FeignTestClientFactory.createClientApi(ProjectApi::class.java, port, config)

    @Test
    @ExpectSelect(33)
    @ExpectInsert(4)
    @ExpectUpdate(3)
    @ExpectDelete(1)
    fun `should apply for the standard call`() {
        assertThat(
            projectApi.createProject(ProjectCreateDTO("acronym", STANDARD_CALL_DETAIL.id))
                .also { DRAFT_PROJECT_ID = it.id!! }
        ).isNotNull
    }


}
