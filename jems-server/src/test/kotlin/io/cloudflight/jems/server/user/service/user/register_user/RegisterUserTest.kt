package io.cloudflight.jems.server.user.service.user.register_user

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.config.AppSecurityProperties
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRegistration
import io.cloudflight.jems.server.user.service.model.UserRole
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

internal class RegisterUserTest : UnitTest() {

    companion object {
        private const val USER_ID = 18L
        private const val USER_ROLE_APPLICANT_ID = 3L
    }

    @MockK
    lateinit var persistence: UserPersistence

    @MockK
    lateinit var passwordEncoder: PasswordEncoder

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

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
        )
        val userChange = UserChange(
            id = 0L,
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRoleId = USER_ROLE_APPLICANT_ID,
        )
        val expectedUser = User(
            id = USER_ID,
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            userRole = UserRole(
                id = USER_ROLE_APPLICANT_ID,
                name = "applicant",
                permissions = emptySet()
            ),
        )

        every { persistence.userRoleExists(USER_ROLE_APPLICANT_ID) } returns true
        every { persistence.emailExists("applicant@interact.eu") } returns false
        val slotPassword = slot<String>()
        every { persistence.create(userChange, capture(slotPassword)) } returns expectedUser

        assertThat(registerUser.registerUser(userRegistration)).isEqualTo(expectedUser)
        assertThat(slotPassword.captured).isEqualTo("hash_my_plain_pass")

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.overrideCurrentUser).isEqualTo(AuditUser(id= USER_ID, email="applicant@interact.eu"))
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.USER_REGISTERED,
            entityRelatedId = USER_ID,
            description = "A new user applicant@interact.eu registered:\n" +
                "email set to 'applicant@interact.eu',\n" +
                "name set to 'Michael',\n" +
                "surname set to 'Schumacher',\n" +
                "userRole set to 'applicant(id=3)'",
        ))
    }

    @Test
    fun `registerUser - no applicant role in the system`() {
        val userRegistration = UserRegistration(
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            password = "my_plain_pass",
        )

        every { persistence.userRoleExists(USER_ROLE_APPLICANT_ID) } returns false

        assertThrows<UserRoleNotFound> { registerUser.registerUser(userRegistration) }
    }

    @Test
    fun `registerUser - email already taken`() {
        val userRegistration = UserRegistration(
            email = "applicant@interact.eu",
            name = "Michael",
            surname = "Schumacher",
            password = "my_plain_pass",
        )

        every { persistence.userRoleExists(USER_ROLE_APPLICANT_ID) } returns true
        every { persistence.emailExists("applicant@interact.eu") } returns true

        assertThrows<UserEmailAlreadyTaken> { registerUser.registerUser(userRegistration) }
    }

}
