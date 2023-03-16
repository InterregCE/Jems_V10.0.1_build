package io.cloudflight.jems.server.notification.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.notification.entity.NotificationEntity
import io.cloudflight.jems.server.notification.model.Notification
import io.cloudflight.jems.server.notification.model.NotificationProject
import io.cloudflight.jems.server.notification.model.NotificationType
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotificationPersistenceProviderTest : UnitTest() {

    @MockK
    lateinit var userRepository: UserRepository
    @MockK
    lateinit var notificationRepository: NotificationRepository

    @InjectMockKs
    lateinit var persistence: NotificationPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(userRepository, notificationRepository)
    }

    @Test
    fun saveNotifications() {
        val userNotifyMe = mockk<UserEntity>()
        every { userNotifyMe.email } returns "notify@me"
        every { userRepository.findAllByEmailInIgnoreCaseOrderByEmail(setOf("notify@me")) } returns listOf(userNotifyMe)
        val savedSlot = slot<Iterable<NotificationEntity>>()
        every { notificationRepository.saveAll(capture(savedSlot)) } returnsArgument 0

        val toSave = listOf(
            Notification(
                email = "notify@me",
                subject = "subject",
                body = "body",
                type = NotificationType.ProjectSubmittedStep1,
                project = NotificationProject(45L, "P0045", "45 acr"),
            )
        )
        persistence.saveNotifications(toSave)
        assertThat(savedSlot.captured).hasSize(1)
        assertThat(savedSlot.captured.first().userEntity).isEqualTo(userNotifyMe)
        assertThat(savedSlot.captured.first().projectId).isEqualTo(45L)
        assertThat(savedSlot.captured.first().projectIdentifier).isEqualTo("P0045")
        assertThat(savedSlot.captured.first().projectAcronym).isEqualTo("45 acr")
        assertThat(savedSlot.captured.first().subject).isEqualTo("subject")
        assertThat(savedSlot.captured.first().body).isEqualTo("body")
        assertThat(savedSlot.captured.first().type).isEqualTo(NotificationType.ProjectSubmittedStep1)
    }
}
