package io.cloudflight.jems.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.user.dto.InputPassword
import io.cloudflight.jems.api.user.dto.InputUserCreate
import io.cloudflight.jems.api.user.dto.InputUserUpdate
import io.cloudflight.jems.server.factory.UserFactory
import io.cloudflight.jems.server.factory.UserFactory.Companion.ADMINISTRATOR_EMAIL
import io.cloudflight.jems.server.factory.UserFactory.Companion.APPLICANT_USER_EMAIL
import io.cloudflight.jems.server.security.ADMINISTRATOR
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
    private lateinit var userFactory: UserFactory;

    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    @Transactional
    fun `list paginated users`() {
        // we already have the admin and programme user => 3 users will be persisted
        userFactory.saveAdminUser("u1")

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
                jsonPath("$.i18nFieldErrors.userRoleId.i18nKey")
                    .value("user.userRoleId.should.not.be.empty")
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
            userFactory.applicantUser.userRole.id!!
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
            userFactory.saveRole(ADMINISTRATOR).id!!
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
            put("/api/user/password/${userFactory.adminUser.id}")
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
            put("/api/user/password/${userFactory.adminUser.id}")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(InputPassword("password_long_enough", null)))
        )
            .andExpect(status().isOk)
    }

}
