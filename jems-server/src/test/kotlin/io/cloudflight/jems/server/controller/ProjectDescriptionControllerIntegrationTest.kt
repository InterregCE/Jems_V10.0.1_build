package io.cloudflight.jems.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.programme.dto.SystemLanguage
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevanceBenefit
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevanceStrategy
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevanceSynergy
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.server.factory.CallFactory
import io.cloudflight.jems.server.factory.ProjectFactory
import io.cloudflight.jems.server.factory.UserFactory
import io.cloudflight.jems.server.factory.UserFactory.Companion.ADMINISTRATOR_EMAIL
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ProjectDescriptionControllerIntegrationTest {

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
    fun `project description read`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFactory.saveProject(userFactory.adminUser, call)

        mockMvc.perform(
            get("/api/project/${project.id}/description/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.projectOverallObjective").isEmpty()) // c1
            .andExpect(jsonPath("$.projectRelevance").isEmpty()) // c2
            .andExpect(jsonPath("$.projectPartnership").isEmpty()) // c3
            .andExpect(jsonPath("$.projectManagement").isEmpty()) // c7
            .andExpect(jsonPath("$.projectLongTermPlans").isEmpty()) // c8
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project description c1 updated`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFactory.saveProject(userFactory.adminUser, call)
        val translationEn = InputTranslation(SystemLanguage.EN, "overall objective")
        val projectDescription = InputProjectOverallObjective(setOf(translationEn))

        mockMvc.perform(
            put("/api/project/${project.id}/description/c1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(projectDescription))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.overallObjective[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.overallObjective[0].translation").value(translationEn.translation!!))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project description c2 updated`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFactory.saveProject(userFactory.adminUser, call)
        val translationEn = InputTranslation(SystemLanguage.ES, "relevancia")
        val benefit = InputProjectRelevanceBenefit(ProjectTargetGroup.Egtc, setOf(translationEn))
        val strategy = InputProjectRelevanceStrategy(ProgrammeStrategy.AtlanticStrategy, setOf(translationEn))
        val synergy = InputProjectRelevanceSynergy(setOf(translationEn), setOf(translationEn))
        val projectDescription = InputProjectRelevance(
            territorialChallenge = setOf(translationEn),
            commonChallenge = setOf(translationEn),
            transnationalCooperation = setOf(translationEn),
            projectBenefits = listOf(benefit),
            projectStrategies = listOf(strategy),
            projectSynergies = listOf(synergy),
            availableKnowledge = setOf(translationEn)
        )

        mockMvc.perform(
            put("/api/project/${project.id}/description/c2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(projectDescription))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.territorialChallenge[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.territorialChallenge[0].translation").value(translationEn.translation!!))
            .andExpect(jsonPath("$.commonChallenge[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.commonChallenge[0].translation").value(translationEn.translation!!))
            .andExpect(jsonPath("$.transnationalCooperation[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.transnationalCooperation[0].translation").value(translationEn.translation!!))
            .andExpect(jsonPath("$.projectBenefits[0].group").value(benefit.group.name))
            .andExpect(jsonPath("$.projectBenefits[0].specification[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.projectBenefits[0].specification[0].translation").value(translationEn.translation!!))
            .andExpect(jsonPath("$.projectStrategies[0].strategy").value(strategy.strategy.toString()))
            .andExpect(jsonPath("$.projectStrategies[0].specification[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.projectStrategies[0].specification[0].translation").value(translationEn.translation!!))
            .andExpect(jsonPath("$.projectSynergies[0].synergy[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.projectSynergies[0].synergy[0].translation").value(translationEn.translation!!))
            .andExpect(jsonPath("$.projectSynergies[0].specification[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.projectSynergies[0].specification[0].translation").value(translationEn.translation!!))
            .andExpect(jsonPath("$.availableKnowledge[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.availableKnowledge[0].translation").value(translationEn.translation!!))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `project description c3 updated`() {
        val call = callFactory.savePublishedCallWithoutPolicy(userFactory.adminUser)
        val project = projectFactory.saveProject(userFactory.adminUser, call)
        val translationEn = InputTranslation(SystemLanguage.DE, "Partnerschaft")
        val projectDescription = InputProjectPartnership(setOf(translationEn))

        mockMvc.perform(
            put("/api/project/" + project.id + "/description/c3")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(projectDescription))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.partnership[0].language").value(translationEn.language.name))
            .andExpect(jsonPath("$.partnership[0].translation").value(translationEn.translation!!))
    }
}
