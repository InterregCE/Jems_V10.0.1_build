package io.cloudflight.ems.controller

import io.cloudflight.ems.factory.UserFactory
import io.cloudflight.ems.factory.UserFactory.Companion.ADMINISTRATOR_EMAIL
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
class UserRoleControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userFactory: UserFactory

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    @Transactional
    fun `list paginated roles`() {
        // we already have the admin and programme user => 4 roles will be persisted
        userFactory.saveRole("r1")

        mockMvc.perform(
            get("/api/role?page=0")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.numberOfElements").value(2))
            .andExpect(status().isOk());

        mockMvc.perform(
            get("/api/role?page=1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(status().isOk());

        mockMvc.perform(
            get("/api/role?sort=name,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.content[0].name").value("r1"))
            .andExpect(status().isOk());
    }
}
