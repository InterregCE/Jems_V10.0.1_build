package io.cloudflight.ems.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.factory.AccountFactory.Companion.ADMINISTRATOR_EMAIL
import net.bytebuddy.utility.RandomString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project created`() {
        val inputProject = InputProject("acronym", LocalDate.now().plusDays(1));

        mockMvc.perform(
            post("/api/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputProject))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.acronym").value(inputProject.acronym.toString()))
            .andExpect(
                jsonPath("$.submissionDate")
                    .value(inputProject.submissionDate.toString())
            )
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project create fails with missing required fields`() {
        val inputProject = InputProject(null, null);

        mockMvc.perform(
            post("/api/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputProject))
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.i18nFieldErrors.acronym.i18nKey")
                    .value("project.acronym.should.not.be.empty")
            )
            .andExpect(
                jsonPath("$.i18nFieldErrors.submissionDate.i18nKey")
                    .value("project.submissionDate.should.not.be.empty")
            );
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project create fails with invalid fields`() {
        val inputProject = InputProject(RandomString.make(26), LocalDate.now().plusDays(1));

        mockMvc.perform(
            post("/api/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputProject))
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.i18nFieldErrors.acronym.i18nKey")
                    .value("project.acronym.size.too.long")
            )
    }
}
