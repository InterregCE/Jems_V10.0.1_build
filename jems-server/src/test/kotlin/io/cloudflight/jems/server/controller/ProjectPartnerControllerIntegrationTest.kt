package io.cloudflight.jems.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.project.dto.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.ProjectPartnerRole
import io.cloudflight.jems.server.factory.CallFactory
import io.cloudflight.jems.server.factory.ProjectFileFactory
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
    private lateinit var jsonMapper: ObjectMapper

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner created`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFileFactory.saveProject(userFactory.adminUser, call)
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER)

        mockMvc.perform(
            post("/api/project/${project.id}/partner")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputProjectPartner))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.name").value(inputProjectPartner.name.toString()))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner create fails with missing required fields`() {
        val inputProjectPartner = InputProjectPartnerCreate(null, null)

        mockMvc.perform(
            post("/api/project/1/partner")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputProjectPartner))
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.i18nFieldErrors.name.i18nKey")
                    .value("project.partner.name.should.not.be.empty")
            )
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project partner create fails with invalid fields`() {
        val inputProjectPartner = InputProjectPartnerCreate(RandomString.make(16), ProjectPartnerRole.LEAD_PARTNER)

        mockMvc.perform(
            post("/api/project/1/partner")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(inputProjectPartner))
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.i18nFieldErrors.name.i18nKey")
                    .value("project.partner.name.size.too.long")
            )
    }
}
