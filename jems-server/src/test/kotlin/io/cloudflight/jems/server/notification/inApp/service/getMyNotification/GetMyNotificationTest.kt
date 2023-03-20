package io.cloudflight.jems.server.notification.inApp.service.getMyNotification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.notification.inApp.service.NotificationPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class GetMyNotificationTest : UnitTest() {

    @MockK
    private lateinit var persistence: NotificationPersistence
    @MockK
    private lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var interactor: GetMyNotification

    @Test
    fun get() {
        every { securityService.getUserIdOrThrow() } returns 512L
        val notification = mockk<UserNotification>()
        every { persistence.getUserNotifications(512L, Pageable.unpaged()) } returns PageImpl(listOf(notification))
        assertThat(interactor.get(Pageable.unpaged())).containsExactly(notification)
    }

}
