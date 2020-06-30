package io.cloudflight.ems.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.ems.api.dto.InputProjectFileDescription
import io.cloudflight.ems.factory.AccountFactory
import io.cloudflight.ems.factory.AccountFactory.Companion.ADMINISTRATOR_EMAIL
import io.cloudflight.ems.factory.AccountFactory.Companion.APPLICANT_USER_EMAIL
import io.cloudflight.ems.factory.ProjectFileFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
class ProjectFileControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    @Autowired
    private lateinit var projectFileFactory: ProjectFileFactory

    @Autowired
    private lateinit var accountFactory: AccountFactory

    @Test
    @Transactional
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project file description set`() {
        val project = projectFileFactory.saveProject(accountFactory.adminAccount)
        val projectFile = projectFileFactory.saveProjectFile(project, accountFactory.adminAccount)

        val projectDescription = InputProjectFileDescription("new test description")

        mockMvc.perform(
            put("/api/project/${project.id}/file/${projectFile.id}/description")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(projectDescription))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(projectFile.id))
            .andExpect(jsonPath("$.description").value(projectDescription.description.toString()))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project file descritpion too long`() {
        val projectDescription =
            InputProjectFileDescription("1234567890____15____1234567890____35____1234567890____55____1234567890____75____1234567890____95____1234567890")

        mockMvc.perform(
            put("/api/project/1/file/10/description")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(projectDescription))
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.i18nFieldErrors.description.i18nKey")
                    .value("project.file.description.size.too.long")
            )
    }

    @Test
    @Transactional
    @WithUserDetails(value = APPLICANT_USER_EMAIL)
    fun `project file access forbidden for non-owner applicants`() {
        val project = projectFileFactory.saveProject(accountFactory.adminAccount)
        val projectFile = projectFileFactory.saveProjectFile(project, accountFactory.adminAccount)

        mockMvc.perform(
            get("/api/project/${project.id}")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isForbidden())

        mockMvc.perform(
            delete("/api/project/${project.id}/file/${projectFile.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden())

        val projectDescription = InputProjectFileDescription("new test description")
        mockMvc.perform(
            put("/api/project/${project.id}/file/${projectFile.id}/description")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(projectDescription))
        )
            .andExpect(status().isForbidden())
    }

    @Test
    @Transactional
    @WithUserDetails(value = APPLICANT_USER_EMAIL)
    fun `project file access allowed for owner applicants`() {
        val project = projectFileFactory.saveProject(accountFactory.applicantAccount)
        val projectFile = projectFileFactory.saveProjectFile(project, accountFactory.applicantAccount)

        mockMvc.perform(
            get("/api/project/${project.id}")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk())
    }
}
