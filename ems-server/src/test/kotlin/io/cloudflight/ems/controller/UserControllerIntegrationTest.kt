package io.cloudflight.ems.controller

import io.cloudflight.ems.factory.AccountFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var accountFactory: AccountFactory;

    @Test
    @WithMockUser(value = "admin")
    @Throws(Exception::class)
    fun list() {
        // we already have the admin user => 3 users will be persisted
        accountFactory.saveAdminAccount("u1")
        accountFactory.saveAdminAccount("u2")

        mockMvc.perform(
            get("/api/users?page=0")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.content.size()").value(2))
            .andExpect(status().isOk());

        mockMvc.perform(
            get("/api/users?page=1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.content.size()").value(1))
            .andExpect(status().isOk());

        mockMvc.perform(
            get("/api/users?sort=email,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.content[0].email").value("u2"))
            .andExpect(status().isOk());
    }
}
