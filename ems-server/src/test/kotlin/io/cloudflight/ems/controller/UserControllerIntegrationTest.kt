package io.cloudflight.ems.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.ems.api.dto.user.InputPassword
import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserUpdate
import io.cloudflight.ems.factory.AccountFactory
import io.cloudflight.ems.factory.AccountFactory.Companion.ADMINISTRATOR_EMAIL
import io.cloudflight.ems.factory.AccountFactory.Companion.APPLICANT_USER_EMAIL
import io.cloudflight.ems.security.ADMINISTRATOR
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
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
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    @Transactional
    fun `list paginated users`() {
        // we already have the admin and programme user => 2 users will be persisted
        accountFactory.saveAdminAccount("u1")

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
            .andExpect(jsonPath("$.content[0].email").value("u1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    @Transactional
    fun `create user`() {
        val user = InputUserCreate("user@rmail.com", "user", "user", 1);

        mockMvc.perform(
            post("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(user))
        )
            .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `create user with invalid data fails`() {
        val user = InputUserCreate("user", "", "", null);

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

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `create user with duplicate email fails`() {
        val user = InputUserCreate(ADMINISTRATOR_EMAIL, ADMINISTRATOR_EMAIL, ADMINISTRATOR_EMAIL, 1);

        mockMvc.perform(
            post("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(user))
        )
            .andExpect(status().isBadRequest())
            .andExpect(
                jsonPath("$.i18nFieldErrors.email.i18nKey")
                    .value("user.email.not.unique")
            )
    }

    @Test
    @WithUserDetails(value = APPLICANT_USER_EMAIL)
    @Transactional
    fun `list, edit create unauthorized for limited user`() {

        mockMvc.perform(
            get("/api/user?page=0")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isForbidden());

        val user = InputUserCreate("random@email.com", "user", "user", 1);
        mockMvc.perform(
            put("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(user))
        )
            .andExpect(status().isForbidden())

        mockMvc.perform(
            post("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(user))
        )
            .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = APPLICANT_USER_EMAIL)
    @Transactional
    fun `edit authorized for user which is current user`() {

        val programmeUser = InputUserUpdate(
            2,
            APPLICANT_USER_EMAIL,
            APPLICANT_USER_EMAIL,
            APPLICANT_USER_EMAIL,
            accountFactory.applicantAccount.accountRole.id!!
        )
        mockMvc.perform(
            put("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(programmeUser))
        )
            .andExpect(status().isOk())
    }

    @Test
    @WithUserDetails(value = APPLICANT_USER_EMAIL)
    @Transactional
    fun `program user edit his role forbid`() {

        val programmeUser = InputUserUpdate(
            2,
            APPLICANT_USER_EMAIL,
            APPLICANT_USER_EMAIL,
            APPLICANT_USER_EMAIL,
            accountFactory.saveRole(ADMINISTRATOR).id!!
        )
        mockMvc.perform(
            put("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(programmeUser))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    @Transactional
    fun `get user by id`() {
        mockMvc.perform(
            get("/api/user/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(ADMINISTRATOR_EMAIL))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `change password short`() {
        mockMvc.perform(
            put("/api/user/${accountFactory.adminAccount.id}")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(InputPassword("short", null)))
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.i18nFieldErrors.password.i18nKey")
                    .value("user.password.size.too.short")
            )
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `change password long`() {
        mockMvc.perform(
            put("/api/user/${accountFactory.adminAccount.id}")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(InputPassword("password_long_enough", null)))
        )
            .andExpect(status().isOk)
    }

}
