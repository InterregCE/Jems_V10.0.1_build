package io.cloudflight.jems.server.common.controller

import io.cloudflight.jems.api.common.InfoApi
import io.cloudflight.platform.context.ApplicationContextProfiles
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(ApplicationContextProfiles.TEST_CONTAINER)
class InfoControllerTest(
    @Autowired @LocalServerPort private val port: Int,
) {

    private val infoApi: InfoApi = FeignTestClientFactory.createClientApi(InfoApi::class.java, port)

    @Test
    fun getVersionInfo() {
        val info = infoApi.getVersionInfo()
        assertThat(info.version).isNotEmpty // e.g. "4.0.0-P-SNAPSHOT" fr local
    }

}
