package io.cloudflight.jems.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudflight.jems.api.user.dto.PasswordDTO
import io.cloudflight.jems.api.user.dto.UserChangeDTO
import io.cloudflight.jems.server.factory.UserFactory
import io.cloudflight.jems.server.factory.UserFactory.Companion.ADMINISTRATOR_EMAIL
import io.cloudflight.jems.server.factory.UserFactory.Companion.APPLICANT_USER_EMAIL
import io.cloudflight.jems.server.authentication.model.ADMINISTRATOR
import org.junit.Ignore
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
    private lateinit var userFactory: UserFactory

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
            .andExpect(status().isOk())

        mockMvc.perform(
            get("/api/user?page=1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(status().isOk())

        mockMvc.perform(
            get("/api/user?sort=email,desc")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(jsonPath("$.content[0].email").value("u1"))
            .andExpect(status().isOk())
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    @Transactional
    fun `create user`() {
        val user = UserChangeDTO(null, "user@rmail.com", "user", "user", 1)

        mockMvc.perform(
            post("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(user))
        )
            .andExpect(status().isOk())
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `create user with invalid data fails`() {
        val user = UserChangeDTO(null, "user", "", "", 0)

        mockMvc.perform(
            post("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(user))
        )
            .andExpect(status().isUnprocessableEntity)
            .andExpect(
                jsonPath("$.formErrors.email.i18nKey")
                    .value("user.email.wrong.format")
            )
            .andExpect(
                jsonPath("$.formErrors.name.i18nKey")
                    .value("common.error.field.blank")
            )
            .andExpect(
                jsonPath("$.formErrors.surname.i18nKey")
                    .value("common.error.field.blank")
            )
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `create user with duplicate email fails`() {
        val user = UserChangeDTO(null, ADMINISTRATOR_EMAIL, ADMINISTRATOR_EMAIL, ADMINISTRATOR_EMAIL, 1)

        mockMvc.perform(
            post("/api/user")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(user))
        )
            .andExpect(status().isUnprocessableEntity)
            .andExpect(
                jsonPath("$.details[0].i18nMessage.i18nKey")
                    .value("use.case.create.user.email.already.in.use")
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
            .andExpect(status().isForbidden())

        val user = UserChangeDTO(null, "random@email.com", "user", "user", 1)
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
            .andExpect(status().isForbidden())
    }

    @Test
    @Ignore("there is now only 1 update and user should not be able to update his role")
    @WithUserDetails(value = APPLICANT_USER_EMAIL)
    @Transactional
    fun `edit authorized for user which is current user`() {
//        val programmeUser = UserChangeDTO(
//            2,
//            APPLICANT_USER_EMAIL,
//            APPLICANT_USER_EMAIL,
//            APPLICANT_USER_EMAIL,
//            userFactory.applicantUser.userRole.id
//        )
//        mockMvc.perform(
//            put("/api/user")
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(jsonMapper.writeValueAsString(programmeUser))
//        )
//            .andExpect(status().isOk())
    }

    @Test
    @WithUserDetails(value = APPLICANT_USER_EMAIL)
    @Transactional
    fun `program user edit his role forbid`() {

        val programmeUser = UserChangeDTO(
            2,
            APPLICANT_USER_EMAIL,
            APPLICANT_USER_EMAIL,
            APPLICANT_USER_EMAIL,
            userFactory.saveRole(ADMINISTRATOR).id
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
            get("/api/user/byId/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(ADMINISTRATOR_EMAIL))
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `change password short`() {
        mockMvc.perform(
            put("/api/user/byId/${userFactory.adminUser.id}/password")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(PasswordDTO("short", null)))
        )
            .andExpect(status().isUnprocessableEntity)
            .andExpect(
                jsonPath("$.formErrors.password.i18nKey")
                    .value("user.password.constraints.not.satisfied")
            )
    }

    @Test
    @WithUserDetails(value = ADMINISTRATOR_EMAIL)
    fun `should return OK when change password to a strong enough password`() {
        mockMvc.perform(
            put("/api/user/byId/${userFactory.adminUser.id}/password")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonMapper.writeValueAsString(PasswordDTO("StrongPa55word", null)))
        )
            .andExpect(status().isOk)
    }

}
