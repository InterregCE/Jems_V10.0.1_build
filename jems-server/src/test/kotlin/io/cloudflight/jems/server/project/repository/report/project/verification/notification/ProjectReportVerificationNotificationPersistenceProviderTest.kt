package io.cloudflight.jems.server.project.repository.report.project.verification.notification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.verification.notification.ProjectReportVerificationNotificationEntity
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

class ProjectReportVerificationNotificationPersistenceProviderTest : UnitTest() {

    companion object {
        private const val REPORT_ID = 10L
        private const val USER_ID = 1L
        private val createdAtLocal = LocalDateTime.now()
        private val createdAt = createdAtLocal.atOffset(UTC).toZonedDateTime()


        private val userEntity = UserEntity(
            id = 1,
            email = "test@email.com",
            sendNotificationsToEmail = false,
            name = "Name",
            surname = "Surname",
            userRole = UserRoleEntity(id = 1, name = "ADMIN"),
            password = "hash_pass",
            userStatus = UserStatus.ACTIVE
        )

        private val user = UserSimple(
            id = 1L,
            name = "Name",
            surname = "Surname",
            email = "test@email.com"
        )

        private val projectReportEntity = mockk<ProjectReportEntity>()
        private val verificationNotificationEntity = ProjectReportVerificationNotificationEntity(
            id = 1L,
            projectReport = projectReportEntity,
            user = userEntity,
            createdAt = createdAtLocal,
        )

        private val expectedVerificationNotification = ProjectReportVerificationNotification(
            id = 1L, reportId = REPORT_ID, triggeredByUser = user, createdAt = createdAt
        )
    }

    @MockK
    lateinit var repository: ProjectReportVerificationNotificationRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var projectReportRepository: ProjectReportRepository

    @InjectMockKs
    lateinit var persistenceProvider: ProjectReportVerificationNotificationPersistenceProvider

    @Test
    fun getLastVerificationNotificationMetaData() {
        every { repository.findTop1ByProjectReportIdOrderByIdDesc(REPORT_ID) } returns verificationNotificationEntity
        every { verificationNotificationEntity.projectReport.id } returns REPORT_ID

        assertThat(
            persistenceProvider.getLastVerificationNotificationMetaData(
                REPORT_ID
            )
        ).isEqualTo(expectedVerificationNotification)
    }

    @Test
    fun storeVerificationNotificationMetaData() {
        every { repository.save(any()) } returns verificationNotificationEntity
        every { userRepository.getById(USER_ID) } returns userEntity
        every { projectReportEntity.id } returns REPORT_ID
        every { projectReportRepository.getById(REPORT_ID) } returns projectReportEntity

        assertThat(persistenceProvider.storeVerificationNotificationMetaData(REPORT_ID, USER_ID, createdAtLocal)).isEqualTo(
            expectedVerificationNotification
        )
    }
}
