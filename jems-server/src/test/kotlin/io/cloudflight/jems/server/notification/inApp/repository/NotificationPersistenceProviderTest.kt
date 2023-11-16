package io.cloudflight.jems.server.notification.inApp.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.notification.inApp.entity.NotificationEntity
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationPartner
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProject
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

class NotificationPersistenceProviderTest : UnitTest() {

    private val TIME = ZonedDateTime.of(
        LocalDate.of(2023, 3, 17),
        LocalTime.of(11, 55, 21, 123456789),
        ZoneId.of("Europe/Paris"),
    )

    @MockK
    lateinit var userRepository: UserRepository
    @MockK
    lateinit var projectRepository: ProjectRepository
    @MockK
    lateinit var notificationRepository: NotificationRepository

    @InjectMockKs
    lateinit var persistence: NotificationPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(userRepository, projectRepository, notificationRepository)
    }

    @Test
    fun saveNotifications() {
        val userNotifyMe = mockk<UserEntity>()
        every { userNotifyMe.email } returns "notify.me@inApp"
        every { userRepository.findAllByEmailInIgnoreCaseOrderByEmail(setOf("notify.me@inApp")) } returns listOf(userNotifyMe)

        val call = mockk<CallEntity>()
        every { call.id } returns 504L
        every { call.name } returns "callName"

        val project = mockk<ProjectEntity>()
        every { project.id } returns 45L
        every { project.call } returns call
        every { projectRepository.getById(45L) } returns project

        val savedSlot = slot<Iterable<NotificationEntity>>()
        every { notificationRepository.saveAll(capture(savedSlot)) } returnsArgument 0

        val groupId = UUID.randomUUID()
        val toSave = NotificationInApp(
            subject = "subject",
            body = "body",
            type = NotificationType.ProjectSubmittedStep1,
            time = TIME,
            templateVariables = mutableMapOf(
                "var1" to "val1",
                "projectId" to 45L,
                "projectIdentifier" to "P0045",
                "projectAcronym" to "45 acr",
            ),
            recipientsInApp = setOf("notify.me@inApp"),
            recipientsEmail = setOf("notify.me@email"),
            emailTemplate = "template.html",
            groupId = groupId,
        )

        val expectedEpochSecond = 1679050521L
        val expectedNano = 123456789

        persistence.saveNotification(toSave)
        assertThat(savedSlot.captured).hasSize(1)
        assertThat(savedSlot.captured.first().groupIdentifier).isEqualTo(groupId)
        assertThat(savedSlot.captured.first().account).isEqualTo(userNotifyMe)
        assertThat(savedSlot.captured.first().created).isEqualTo(LocalDateTime.ofEpochSecond(expectedEpochSecond, expectedNano, ZoneOffset.UTC))
        assertThat(savedSlot.captured.first().project).isEqualTo(project)
        assertThat(savedSlot.captured.first().projectIdentifier).isEqualTo("P0045")
        assertThat(savedSlot.captured.first().projectAcronym).isEqualTo("45 acr")
        assertThat(savedSlot.captured.first().subject).isEqualTo("subject")
        assertThat(savedSlot.captured.first().body).isEqualTo("body")
        assertThat(savedSlot.captured.first().type).isEqualTo(NotificationType.ProjectSubmittedStep1)

        assertThat(savedSlot.captured.first().created.toEpochSecond(ZoneOffset.UTC)).isEqualTo(expectedEpochSecond)
        assertThat(savedSlot.captured.first().created.nano).isEqualTo(expectedNano)

        assertThat(savedSlot.captured.first().toModel().time.toEpochSecond()).isEqualTo(expectedEpochSecond)
        assertThat(savedSlot.captured.first().toModel().time.nano).isEqualTo(expectedNano)
    }

    @Test
    fun getUserNotifications() {
        val epoch = 1679053859L
        val time = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC)
        val user = mockk<UserEntity>()
        val call = mockk<CallEntity>()
        every { call.id } returns 55L
        every { call.name } returns "call-55"
        val project = mockk<ProjectEntity>()
        every { project.id } returns 14L
        every { project.call } returns call
        val notification = NotificationEntity(
            id = 7L,
            groupIdentifier = UUID.randomUUID(),
            instanceIdentifier = UUID.randomUUID(),
            account = user,
            created = time,
            project = project,
            projectIdentifier = "C-P0014",
            projectAcronym = "I want money",
            partnerId = 321L,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,
            subject = "Project was submitted",
            body = "Project was submitted. Your JEMS",
            type = NotificationType.ProjectSubmittedStep1,
        )
        every { notificationRepository.findAllByAccountId(15L, Pageable.unpaged()) } returns
            PageImpl(listOf(notification))

        val transformedBack = persistence.getUserNotifications(15L, Pageable.unpaged()).first()
        assertThat(transformedBack.time.toEpochSecond()).isEqualTo(epoch)
        assertThat(transformedBack).isEqualTo(
            UserNotification(
                id = 7L,
                project = NotificationProject(55L, "call-55",14L, "C-P0014", "I want money"),
                partner = NotificationPartner(321L, ProjectPartnerRole.LEAD_PARTNER, 1),
                time = Instant.ofEpochSecond(epoch).atOffset(ZoneOffset.UTC).toZonedDateTime(),
                subject = "Project was submitted",
                body = "Project was submitted. Your JEMS",
                type = NotificationType.ProjectSubmittedStep1,
            )
        )
    }

    @Test
    fun saveOrUpdateSystemNotification() {
        val emails = setOf("email1, email2")
        val user1 = mockk<UserEntity>()
        val user2 = mockk<UserEntity>()
        val groupId = UUID.randomUUID()

        every { userRepository.findAllByEmailInIgnoreCaseOrderByEmail(emails) } returns
                listOf(user1, user2)

        every { notificationRepository.deleteAllByGroupIdentifier(groupId) } answers { }
        val slotSaved = slot<Iterable<NotificationEntity>>()
        every { notificationRepository.saveAll(capture(slotSaved)) } returnsArgument 0

        val toSave = NotificationInApp(
            subject = "subject",
            body = "body",
            type = NotificationType.SystemMessage,
            time = TIME,
            templateVariables = mockk(),
            recipientsInApp = emails,
            recipientsEmail = emptySet(),
            emailTemplate = null,
            groupId = groupId,
        )

        persistence.saveOrUpdateSystemNotification(toSave)

        verify(exactly = 1) { notificationRepository.deleteAllByGroupIdentifier(groupId) }
        assertThat(slotSaved.captured).hasSize(2)
        assertThat(slotSaved.captured.first().account).isEqualTo(user1)
        assertThat(slotSaved.captured.first().groupIdentifier).isEqualTo(groupId)
        assertThat(slotSaved.captured.first().subject).isEqualTo("subject")
        assertThat(slotSaved.captured.first().body).isEqualTo("body")
        assertThat(slotSaved.captured.last().account).isEqualTo(user2)
        assertThat(slotSaved.captured.last().groupIdentifier).isEqualTo(groupId)
        assertThat(slotSaved.captured.last().subject).isEqualTo("subject")
        assertThat(slotSaved.captured.last().body).isEqualTo("body")
    }

}
