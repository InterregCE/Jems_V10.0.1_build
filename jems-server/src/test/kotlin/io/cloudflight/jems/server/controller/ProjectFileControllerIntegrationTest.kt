package io.cloudflight.jems.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.project.dto.file.ProjectFileCategoryTypeDTO
import io.cloudflight.jems.server.factory.CallFactory
import io.cloudflight.jems.server.factory.ProjectFileFactory
import io.cloudflight.jems.server.factory.UserFactory
import io.cloudflight.jems.server.factory.UserFactory.Companion.ADMINISTRATOR_EMAIL
import io.cloudflight.jems.server.factory.UserFactory.Companion.APPLICANT_USER_EMAIL
import io.cloudflight.jems.server.utils.PARTNER_ID
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
    private lateinit var callFactory: CallFactory

    @Autowired
    private lateinit var userFactory: UserFactory

    @Test
    @Transactional
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project file description set`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFileFactory.saveProject(userFactory.adminUser, call)
        val projectFile = projectFileFactory.saveProjectFile(project, userFactory.adminUser)

        val description = "new test description"
        mockMvc.perform(
            put("/api/project/${project.id}/file/${projectFile.id}/description")
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .content(description)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(projectFile.id.toString()))
            .andExpect(jsonPath("$.description").value(description))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project file description too long`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFileFactory.saveProject(userFactory.adminUser, call)
        val projectFile = projectFileFactory.saveProjectFile(project, userFactory.adminUser)

        val projectDescription = "1234567890____15____1234567890____35____1234567890____55____1234567890____75____1234567890____95____1234567890"

        mockMvc.perform(
            put("/api/project/${project.id}/file/${projectFile.id}/description")
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .content(projectDescription)
        )
            .andExpect(status().isUnprocessableEntity)
            .andExpect(
                jsonPath("$.formErrors.description.i18nKey")
                    .value("common.error.field.max.length")
            )
    }

    @Test
    @Transactional
    @WithUserDetails(value = APPLICANT_USER_EMAIL)
    fun `project file not found for non-owner applicants`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFileFactory.saveProject(userFactory.adminUser, call)
        val projectFile = projectFileFactory.saveProjectFile(project, userFactory.adminUser)

        mockMvc.perform(
            get("/api/project/byId/${project.id}")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNotFound())

        mockMvc.perform(
            delete("/api/project/${project.id}/file/${projectFile.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound())

        val projectDescription = "new test description"
        mockMvc.perform(
            put("/api/project/${project.id}/file/${projectFile.id}/description")
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .content(jsonMapper.writeValueAsString(projectDescription))
        )
            .andExpect(status().isNotFound())
    }

    @Test
    @Transactional
    @WithUserDetails(value = APPLICANT_USER_EMAIL)
    fun `project file access allowed for owner applicants`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFileFactory.saveProject(userFactory.applicantUser, call)
        mockMvc.perform(
            get("/api/project/byId/${project.id}")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk())
    }


    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `should return list of project file metadata`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFileFactory.saveProject(userFactory.adminUser, call)
        val projectFile = projectFileFactory.saveProjectFile(project, userFactory.adminUser)

        mockMvc.perform(
            get("/api/project/${project.id}/file/list")
                .queryParam("type", ProjectFileCategoryTypeDTO.PARTNER.name)
                .queryParam("id", PARTNER_ID.toString())
                .queryParam("sort", "asc")
                .queryParam("size", "2")
                .queryParam("page", "0")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(jsonPath("$.content[0].id").value(projectFile.id))
    }
}
