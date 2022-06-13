package io.cloudflight.jems.server.user.service.user.register_user

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.captcha.CaptchaService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.config.AppCaptchaProperties
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.confirmation.UserConfirmationPersistence
import io.cloudflight.jems.server.user.service.model.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import javax.servlet.http.HttpSession

internal class RegisterUserTest : UnitTest() {

    companion object {
        private const val USER_ID = 18L
        private const val defaultUserRoleId = 3L
    }

    @MockK
    lateinit var persistence: UserPersistence

    @MockK
    lateinit var programmeDataPersistence: ProgrammeDataPersistence

    @MockK
    lateinit var passwordEncoder: PasswordEncoder

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var userConfirmationPersistence: UserConfirmationPersistence

    @RelaxedMockK
    lateinit var appCaptchaProperties: AppCaptchaProperties

    @RelaxedMockK
    lateinit var HttpSession: HttpSession

    @RelaxedMockK
    lateinit var captchaService: CaptchaService

    @InjectMockKs
    lateinit var registerUser: RegisterUser

    @BeforeAll
    fun setup() {
        every { passwordEncoder.encode(any()) } answers { "hash_${firstArg<String>()}" }
    }

    @Test
    fun registerUser() {
        val userRegistration = UserRegistration(
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            password = "my_plain_pass",
            captcha = "testCaptcha"
        )
        val userChange = UserChange(
            id = 0L,
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRoleId = defaultUserRoleId,
            userStatus = UserStatus.UNCONFIRMED
        )
        val expectedUser = User(
            id = USER_ID,
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRole = UserRole(
                id = defaultUserRoleId,
                name = "applicant",
                permissions = emptySet()
            ),
            userStatus = UserStatus.UNCONFIRMED
        )

        every { programmeDataPersistence.getDefaultUserRole() } returns defaultUserRoleId
        every { persistence.userRoleExists(defaultUserRoleId) } returns true
        every { persistence.emailExists("applicant@interact.eu") } returns false
        val slotPassword = slot<String>()
        every { persistence.create(userChange, capture(slotPassword)) } returns expectedUser

        assertThat(registerUser.registerUser(userRegistration)).isEqualTo(expectedUser)
        assertThat(slotPassword.captured).isEqualTo("hash_my_plain_pass")

        val slotAudit = slot<UserRegisteredEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.user).isEqualTo(expectedUser)
    }

    @Test
    fun `registerUser - no default role set`() {
        val userRegistration = UserRegistration(
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            password = "my_plain_pass",
            captcha = "testCaptcha"
        )
        every { programmeDataPersistence.getDefaultUserRole() } returns null

        assertThrows<DefaultUserRoleNotFound> { registerUser.registerUser(userRegistration) }
    }

    @Test
    fun `registerUser - default role is not specified`() {
        val userRegistration = UserRegistration(
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            password = "my_plain_pass",
            captcha = "testCaptcha"
        )
        every { programmeDataPersistence.getDefaultUserRole() } returns null

        assertThrows<DefaultUserRoleNotFound> { registerUser.registerUser(userRegistration) }
    }

    @Test
    fun `registerUser - email already taken`() {
        val userRegistration = UserRegistration(
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            password = "my_plain_pass",
            captcha = "testCaptcha"
        )
        every { programmeDataPersistence.getDefaultUserRole() } returns defaultUserRoleId
        every { persistence.userRoleExists(defaultUserRoleId) } returns true
        every { persistence.emailExists("applicant@interact.eu") } returns true

        assertThrows<UserEmailAlreadyTaken> { registerUser.registerUser(userRegistration) }
    }

}
