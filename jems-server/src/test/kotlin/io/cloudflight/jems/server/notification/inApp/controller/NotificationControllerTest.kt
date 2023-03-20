package io.cloudflight.jems.server.notification.inApp.controller

import io.cloudflight.jems.api.notification.dto.NotificationDTO
import io.cloudflight.jems.api.notification.dto.NotificationProjectDTO
import io.cloudflight.jems.api.notification.dto.NotificationTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.notification.inApp.service.getMyNotification.GetMyNotificationInteractor
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProject
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

class NotificationControllerTest : UnitTest() {

    companion object {
        private val time = ZonedDateTime.now()

        private val notification = UserNotification(
            id = 17L,
            project = NotificationProject(
                callId = 225L,
                callName = "call-225",
                projectId = 400L,
                projectIdentifier = "proj-iden",
                projectAcronym = "proj-acr"
            ),
            time = time,
            subject = "title",
            body = "was submitted",
            type = NotificationType.ProjectSubmitted,
        )

        private val expectedNotification = NotificationDTO(
            id = 17L,
            project = NotificationProjectDTO(
                callId = 225L,
                callName = "call-225",
                projectId = 400L,
                projectIdentifier = "proj-iden",
                projectAcronym = "proj-acr"
            ),
            time = time,
            subject = "title",
            body = "was submitted",
            type = NotificationTypeDTO.ProjectSubmitted,
        )
    }

    @MockK
    private lateinit var getMyNotification: GetMyNotificationInteractor

    @InjectMockKs
    private lateinit var controller: NotificationController

    @Test
    fun getMyNotifications() {
        every { getMyNotification.get(Pageable.unpaged()) } returns PageImpl(listOf(notification))
        assertThat(controller.getMyNotifications(Pageable.unpaged())).containsExactly(expectedNotification)
    }

}
