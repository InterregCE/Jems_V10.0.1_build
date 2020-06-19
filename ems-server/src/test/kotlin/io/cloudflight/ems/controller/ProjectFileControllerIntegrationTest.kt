package io.cloudflight.ems.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.ems.api.dto.InputProjectFileDescription
import io.cloudflight.ems.factory.ProjectFileFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ProjectFileControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    @Autowired
    private lateinit var projectFileFactory: ProjectFileFactory

    @Test
    @WithUserDetails(value = "admin")
    fun `project file description set`() {
        val project = projectFileFactory.saveProject()
        val projectFile = projectFileFactory.saveProjectFile(project)

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
    @WithUserDetails(value = "admin")
    fun `project file descritpion too long`() {
        val projectDescription = InputProjectFileDescription("1234567890____15____1234567890____35____1234567890____55____1234567890____75____1234567890____95____1234567890")

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

}
