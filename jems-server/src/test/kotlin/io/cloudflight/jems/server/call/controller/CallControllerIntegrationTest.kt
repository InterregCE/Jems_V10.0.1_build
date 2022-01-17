package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.CallApi
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.platform.context.ApplicationContextProfiles
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectSelect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.test.context.ActiveProfiles
import java.time.ZonedDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(ApplicationContextProfiles.TEST_CONTAINER)
class CallControllerIntegrationTest(
    @Autowired @LocalServerPort private val port: Int,
) {

    companion object {
        private val START_DATE = ZonedDateTime.now().minusDays(1)
        private val END_DATE = ZonedDateTime.now().plusDays(1)
    }

    private val config =   AnnotationConfigApplicationContext(
        FeignAutoConfiguration::class.java,
        CustomFeignClientConfiguration::class.java,
        HttpMessageConvertersAutoConfiguration::class.java
    )

    private val callApi = FeignTestClientFactory.createClientApi(CallApi::class.java, port, config)

    @BeforeEach
    fun importData() {
        if (callApi.listCalls().isEmpty()) {
            callApi.createCall(
                CallUpdateRequestDTO(
                    name = "Call 1",
                    startDateTime = START_DATE,
                    endDateTime = END_DATE,
                    additionalFundAllowed = true,
                    lengthOfPeriod = 6,
                    description = setOf(InputTranslation(EN, "EN desc")),
                )
            )
        }
    }

    @Test
    // TODO solve performance issue (use Joins?)
    @ExpectSelect(5)   // call, form field configuration, stateAids, translations
    fun getCalls() {
        val calls = callApi.listCalls()
        assertThat(calls).hasSize(1)
        assertThat(calls).containsExactly(IdNamePairDTO(1, "Call 1"))
    }

    @Test
    // TODO solve performance issue
    @ExpectSelect(14)
    fun getCallById() {
        assertThat(callApi.getCallById(1)).isNotNull
    }

}
