package io.cloudflight.ems.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.ems.api.dto.InputUser
import io.cloudflight.ems.factory.AccountFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var accountFactory: AccountFactory

    @Autowired
    private lateinit var jsonMapper: ObjectMapper


    @Test
    @WithMockUser(value = "admin")
    @Transactional
    fun `list paginated users`() {
        // we already have the admin user => 3 users will be persisted
        accountFactory.saveAdminAccount("u1")
        accountFactory.saveAdminAccount("u2")

        mockMvc.perform(
            get("/api/user?page=0")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.numberOfElements").value(2))
            .andExpect(status().isOk());

        mockMvc.perform(
            get("/api/user?page=1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(status().isOk());

        mockMvc.perform(
            get("/api/user?sort=email,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.content[0].email").value("u2"))
            .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "admin")
    @Transactional
    fun `create user`() {
        val user = InputUser("user@rmail.com", "user", "user", 1);

        mockMvc.perform(
            post("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(user))
        )
            .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "admin")
    fun `create user with invalid data fails`() {
        val user = InputUser("user", "u", "u", null);

        mockMvc.perform(
            post("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(user))
        )
            .andExpect(status().isBadRequest())
            .andExpect(
                jsonPath("$.i18nFieldErrors.email.i18nKey")
                    .value("user.email.wrong.format")
            )
            .andExpect(
                jsonPath("$.i18nFieldErrors.name.i18nKey")
                    .value("user.name.wrong.size")
            )
            .andExpect(
                jsonPath("$.i18nFieldErrors.surname.i18nKey")
                    .value("user.surname.wrong.size")
            )
            .andExpect(
                jsonPath("$.i18nFieldErrors.accountRoleId.i18nKey")
                    .value("user.accountRoleId.should.not.be.empty")
            )
    }
}
