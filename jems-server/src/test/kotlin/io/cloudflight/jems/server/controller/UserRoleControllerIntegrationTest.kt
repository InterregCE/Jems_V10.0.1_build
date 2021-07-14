package io.cloudflight.jems.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.user.dto.UserRoleDTO
import io.cloudflight.jems.server.factory.ProgrammeDataFactory
import io.cloudflight.jems.server.factory.UserFactory
import io.cloudflight.jems.server.factory.UserFactory.Companion.ADMINISTRATOR_EMAIL
import io.cloudflight.jems.server.user.service.model.UserRolePermission
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
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
class UserRoleControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    @Autowired
    private lateinit var userFactory: UserFactory
    @Autowired
    private lateinit var programmeDataFactory: ProgrammeDataFactory

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    @Transactional
    fun `list paginated roles`() {
        // we already have the admin and programme roles => 3 roles will be persisted
        userFactory.saveRole("r1", listOf(UserRolePermission.RoleRetrieve))
        programmeDataFactory.saveProgrammeData()

        mockMvc.perform(
            get("/api/role?page=0")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.numberOfElements").value(2))
            .andExpect(status().isOk);

        mockMvc.perform(
            get("/api/role?page=1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(status().isOk);

        mockMvc.perform(
            get("/api/role?sort=name,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.content[0].name").value("r1"))
            .andExpect(status().isOk)
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    @Transactional
    fun `update default role`() {
        val userRoleEntity = userFactory.saveRole("defaultRole", emptyList(), false)
        val userRole = UserRoleDTO(
            id = userRoleEntity.id,
            name = userRoleEntity.name,
            isDefault = true,
            permissions = emptyList()
        )
        programmeDataFactory.saveProgrammeData()

        mockMvc.perform(
            put("/api/role/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(userRole))
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/api/role/byId/${userRole.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.name").value("defaultRole"))
            .andExpect(jsonPath("$.isDefault").value(true))
            .andExpect(status().isOk)
    }
}
