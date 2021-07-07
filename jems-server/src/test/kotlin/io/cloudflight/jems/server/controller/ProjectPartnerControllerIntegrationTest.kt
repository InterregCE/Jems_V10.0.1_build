package io.cloudflight.jems.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.factory.CallFactory
import io.cloudflight.jems.server.factory.ProgrammeDataFactory
import io.cloudflight.jems.server.factory.ProjectFactory
import io.cloudflight.jems.server.factory.ProjectFileFactory
import io.cloudflight.jems.server.factory.ProjectPartnerFactory
import io.cloudflight.jems.server.factory.UserFactory
import io.cloudflight.jems.server.factory.UserFactory.Companion.ADMINISTRATOR_EMAIL
import net.bytebuddy.utility.RandomString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ProjectPartnerControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var callFactory: CallFactory

    @Autowired
    private lateinit var userFactory: UserFactory

    @Autowired
    private lateinit var projectFileFactory: ProjectFileFactory

    @Autowired
    private lateinit var projectFactory: ProjectFactory

    @Autowired
    private lateinit var projectPartnerFactory: ProjectPartnerFactory

    @Autowired
    private lateinit var programmeDataFactory: ProgrammeDataFactory

    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner created`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFileFactory.saveProject(userFactory.adminUser, call)
        programmeDataFactory.saveLegalStatus()
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)

        mockMvc.perform(
            post("/api/project/partner/toProjectId/${project.id}")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputProjectPartner))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.abbreviation").value(inputProjectPartner.abbreviation.toString()))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner create fails with missing required fields`() {
        val inputProjectPartner = InputProjectPartnerCreate(null, null, legalStatusId = null)

        mockMvc.perform(
            post("/api/project/partner/toProjectId/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputProjectPartner))
        )
            .andExpect(status().isUnprocessableEntity)
            .andExpect(
                jsonPath("$.i18nMessage.i18nKey")
                    .value("use.case.create.project.partner.failed")
            )
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner create fails with invalid fields`() {
        val inputProjectPartner = InputProjectPartnerCreate(RandomString.make(16), ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)

        mockMvc.perform(
            post("/api/project/partner/toProjectId/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputProjectPartner))
        )
            .andExpect(status().isUnprocessableEntity)
            .andExpect(
                jsonPath("$.i18nMessage.i18nKey")
                    .value("use.case.create.project.partner.failed")
            )
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner get list`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFactory.saveProject(userFactory.adminUser, call)
        val projectPartner = projectPartnerFactory.saveProjectPartner(userFactory.adminUser, project)

        mockMvc.perform(
            get("/api/project/partner/byProjectId/${project.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(jsonPath("$.content[0].id").value(projectPartner.id))
            .andExpect(jsonPath("$.content[0].abbreviation").value(projectPartner.abbreviation))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner get list by ids`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFactory.saveProject(userFactory.adminUser, call)
        val projectPartner = projectPartnerFactory.saveProjectPartner(userFactory.adminUser, project)

        mockMvc.perform(
            get("/api/project/partner/byProjectId/${project.id}/ids")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(projectPartner.id))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner get`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFactory.saveProject(userFactory.adminUser, call)
        val projectPartner = projectPartnerFactory.saveProjectPartner(userFactory.adminUser, project)

        mockMvc.perform(
            get("/api/project/partner/${projectPartner.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(projectPartner.id))
            .andExpect(jsonPath("$.abbreviation").value(projectPartner.abbreviation))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner not found`() {
        mockMvc.perform(
            get("/api/project/partner/0")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isInternalServerError)
            .andExpect(
                jsonPath("$.i18nMessage.i18nKey")
                    .value("use.case.get.project.partner.by.id.failed")
            )
    }
}
