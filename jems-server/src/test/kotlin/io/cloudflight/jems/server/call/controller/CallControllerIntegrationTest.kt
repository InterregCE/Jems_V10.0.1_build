package io.cloudflight.jems.server.call.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.call.dto.InputCallCreate
import io.cloudflight.jems.server.factory.UserFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@SpringBootTest
@AutoConfigureMockMvc
class CallControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: ObjectMapper


    @Test
    @WithUserDetails(value = UserFactory.ADMINISTRATOR_EMAIL)
    @Transactional
    fun `create call`() {
        val call = InputCallCreate(
            "New Call",
            null,
            null,
            false,
            null,
            ZonedDateTime.now(),
            ZonedDateTime.now().plusDays(3L),
            "Short description",
            12
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/call")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(call))
        )
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
