package io.cloudflight.jems.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.user.dto.UserRegistrationDTO
import io.cloudflight.jems.server.factory.UserFactory.Companion.ADMINISTRATOR_EMAIL
import org.junit.Ignore
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    @Test
    @Ignore("role not found")
    @Transactional
    fun `register applicant`() {
//        val userRegistration = UserRegistrationDTO(
//            "e@e", "name", "surname",
//            "ApplicantPa55"
//        )
//        mockMvc.perform(
//            post("/api/registration")
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(jsonMapper.writeValueAsString(userRegistration))
//        )
//            .andExpect(status().isOk())
    }

    @Test
    @Ignore("role not found")
    fun `register user with duplicate email fails`() {
//        val user = UserRegistrationDTO(ADMINISTRATOR_EMAIL, ADMINISTRATOR_EMAIL, ADMINISTRATOR_EMAIL, "pwd")
//
//        mockMvc.perform(
//            post("/api/registration")
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(jsonMapper.writeValueAsString(user))
//        )
//            .andExpect(status().isBadRequest())
//            .andExpect(
//                jsonPath("$.formErrors.email.i18nKey")
//                    .value("user.email.not.unique")
//            )
    }
}
