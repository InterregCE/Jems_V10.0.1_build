package io.cloudflight.jems.server.common.controller

import io.cloudflight.jems.api.common.dto.VersionDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.platform.server.ServerModuleIdentification
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.info.InfoEndpoint
import java.util.UUID

internal class InfoControllerTest : UnitTest() {

    companion object {
        private val COMMIT_ID: String = UUID.randomUUID().toString()
    }

    @MockK
    lateinit var serverModuleIdentification: ServerModuleIdentification

    @MockK
    lateinit var infoEndpoint: InfoEndpoint

    @InjectMockKs
    private lateinit var controller: InfoController

    @Test
    fun getVersionInfo() {
        every { serverModuleIdentification.getVersion() } returns "1.1.0"
        every { serverModuleIdentification.getId() } returns COMMIT_ID
        every { infoEndpoint.info() } returns mapOf("helpdesk-url" to "https://unit-test.mock/")

        assertThat(controller.getVersionInfo()).isEqualTo(
            VersionDTO(
                version = "1.1.0",
                commitId = COMMIT_ID,
                helpdeskUrl = "https://unit-test.mock/"
            )
        )
    }

}
