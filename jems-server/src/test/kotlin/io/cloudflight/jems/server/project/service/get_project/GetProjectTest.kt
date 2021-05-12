package io.cloudflight.jems.server.project.service.get_project

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class GetProjectTest : UnitTest() {

    companion object {
        val startDate: ZonedDateTime = ZonedDateTime.now().minusDays(2)
        val endDate: ZonedDateTime = ZonedDateTime.now().plusDays(2)

        val callSettings = ProjectCallSettings(
            callId = 15,
            callName = "Call 15",
            startDate = startDate,
            endDate = endDate,
            endDateStep1 = null,
            lengthOfPeriod = 6,
            isAdditionalFundAllowed = false,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
        )
    }

    @MockK
    lateinit var persistence: ProjectPersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getProject: GetProject

    @Test
    fun getProjectCallSettings() {
        every { persistence.getProjectCallSettings(1L) } returns callSettings
        assertThat(getProject.getProjectCallSettings(1L)).isEqualTo(callSettings.copy())
    }

}
