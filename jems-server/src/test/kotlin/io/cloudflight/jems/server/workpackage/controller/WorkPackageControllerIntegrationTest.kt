package io.cloudflight.jems.server.workpackage.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.server.factory.CallFactory
import io.cloudflight.jems.server.factory.ProjectFactory
import io.cloudflight.jems.server.factory.UserFactory
import io.cloudflight.jems.server.factory.UserFactory.Companion.ADMINISTRATOR_EMAIL
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@AutoConfigureMockMvc
class WorkPackageControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var callFactory: CallFactory

    @Autowired
    private lateinit var userFactory: UserFactory

    @Autowired
    private lateinit var projectFactory: ProjectFactory

    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `work package created`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFactory.saveProject(userFactory.adminUser, call)
        val translationEn = InputTranslation(SystemLanguage.EN, "Work package name")

        val inputWorkPackage = InputWorkPackageCreate(setOf(translationEn), setOf(), setOf())

        mockMvc.perform(
            post("/api/project/workPackage/forProject/${project.id}")

                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputWorkPackage))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name[0].language").value(translationEn.language.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name[0].translation").value(translationEn.translation!!))
            .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(1))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `work packages received for project id`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFactory.saveProject(userFactory.adminUser, call)

        val firstWorkPackage = InputWorkPackageCreate(setOf(InputTranslation(SystemLanguage.EN, "Work package name")), setOf(), setOf())

        mockMvc.perform(
            post("/api/project/workPackage/forProject/${project.id}")

                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(firstWorkPackage))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        mockMvc.perform(
            get("/api/project/workPackage/perProject/${project.id}")
        )
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfElements").value(1))
    }


}
