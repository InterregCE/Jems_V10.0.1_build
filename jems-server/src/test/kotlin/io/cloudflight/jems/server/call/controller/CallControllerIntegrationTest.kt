package io.cloudflight.jems.server.call.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.call.dto.AllowRealCostsDTO
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.call.service.create_call.CreateCallInteractor
import io.cloudflight.jems.server.call.service.model.Call
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

    @Autowired
    private lateinit var createCallInteractor: CreateCallInteractor

    @Test
    @WithUserDetails(value = UserFactory.ADMINISTRATOR_EMAIL)
    @Transactional
    fun `create call`() {
        val call = CallUpdateRequestDTO(
            name = "New Call",
            startDateTime = ZonedDateTime.now(),
            endDateTime = ZonedDateTime.now().plusDays(3L),
            additionalFundAllowed = false,
            lengthOfPeriod = 12,
            description = setOf(InputTranslation(SystemLanguage.EN, "Short description")),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/call")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(call))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @WithUserDetails(value = UserFactory.ADMINISTRATOR_EMAIL)
    @Transactional
    fun `update allow real costs`() {
        val call = createCallInteractor.createCallInDraft(
            Call(
                name = "New Call",
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now().plusDays(3L),
                isAdditionalFundAllowed = false,
                lengthOfPeriod = 12,
                description = setOf(InputTranslation(SystemLanguage.EN, "Short description"))
            )
        )

        val updateRealCosts = AllowRealCostsDTO(
            allowRealStaffCosts = false,
            allowRealTravelAndAccommodationCosts = true,
            allowRealExternalExpertiseAndServicesCosts = false,
            allowRealEquipmentCosts = true,
            allowRealInfrastructureCosts = false
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/call/byId/${call.id}/allowRealCosts")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(updateRealCosts))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealStaffCosts").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealTravelAndAccommodationCosts").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealExternalExpertiseAndServicesCosts").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealEquipmentCosts").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealInfrastructureCosts").value(false))
    }

    @Test
    @WithUserDetails(value = UserFactory.ADMINISTRATOR_EMAIL)
    @Transactional
    fun `get allow real costs`() {
        val call = createCallInteractor.createCallInDraft(
            Call(
                name = "New Call",
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now().plusDays(3L),
                isAdditionalFundAllowed = false,
                lengthOfPeriod = 12,
                description = setOf(InputTranslation(SystemLanguage.EN, "Short description"))
            )
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/call/byId/${call.id}/allowRealCosts")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealStaffCosts").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealTravelAndAccommodationCosts").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealExternalExpertiseAndServicesCosts").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealEquipmentCosts").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.allowRealInfrastructureCosts").value(true))
    }
}
