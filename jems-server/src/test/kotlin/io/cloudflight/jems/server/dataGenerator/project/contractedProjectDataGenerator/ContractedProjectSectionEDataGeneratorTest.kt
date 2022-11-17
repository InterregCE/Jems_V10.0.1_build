package io.cloudflight.jems.server.dataGenerator.project.contractedProjectDataGenerator

import io.cloudflight.jems.api.project.dto.lumpsum.ProjectLumpSumDTO
import io.cloudflight.jems.api.project.dto.lumpsum.ProjectPartnerLumpSumDTO
import io.cloudflight.jems.api.project.lumpsum.ProjectLumpSumApi
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_LP
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_LUMP_SUMS
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_PP
import io.cloudflight.jems.server.dataGenerator.PROGRAMME_LUMP_SUMS
import io.cloudflight.jems.server.dataGenerator.PROJECT_DATA_INITIALIZER_ORDER
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.boot.web.server.LocalServerPort
import java.math.BigDecimal


@Order(PROJECT_DATA_INITIALIZER_ORDER + 40)
class ContractedProjectSectionEDataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

    private val lumpSumsApi =
        FeignTestClientFactory.createClientApi(ProjectLumpSumApi::class.java, port, config)

    @Test
    @ExpectSelect(26)
    @ExpectInsert(3)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should add lump sums to the project`() {
        CONTRACTED_PROJECT_LUMP_SUMS = lumpSumsApi.updateProjectLumpSums(
            CONTRACTED_PROJECT_ID, listOf(
                ProjectLumpSumDTO(
                    orderNr = 1,
                    programmeLumpSumId = PROGRAMME_LUMP_SUMS.first().id!!,
                    period = 1,
                    lumpSumContributions = listOf(
                        ProjectPartnerLumpSumDTO(
                            CONTRACTED_PROJECT_LP.id,
                            BigDecimal.valueOf(2345, 2),
                        ),
                        ProjectPartnerLumpSumDTO(
                            CONTRACTED_PROJECT_PP.id,
                            BigDecimal.valueOf(162591, 2),
                        )
                    ),
                    readyForPayment = false,
                    comment = null,
                    fastTrack = false
                )
            )
        )
        assertThat(CONTRACTED_PROJECT_LUMP_SUMS).isNotNull
    }

}
