package io.cloudflight.jems.server.project.repository.report.partner.procurement.attachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.project.entity.report.partner.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.partner.procurement.file.ProjectPartnerReportProcurementFileEntity
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class ProjectPartnerReportProcurementAttachmentPersistenceProviderTest : UnitTest() {

    companion object {
        private val YEARS_AGO_20 = ZonedDateTime.now().minusYears(20)

        private fun dummyEntity(
            procurement: ProjectPartnerReportProcurementEntity,
            id: Long = 18L,
            createdIn: Long = 118L,
        ) = ProjectPartnerReportProcurementFileEntity(
            id = id,
            procurement = procurement,
            createdInReportId = createdIn,
            file = JemsFileMetadataEntity(
                id = 658L,
                projectId = 189L,
                partnerId = 391L,
                path = "not-used",
                minioBucket = "not-used also",
                minioLocation = "not-used as well",
                name = "filename.ext",
                type = JemsFileType.ProcurementAttachment,
                size = 989656189L,
                user = UserEntity(
                    id = 45L,
                    name = "Admin",
                    password = "hash",
                    email = "admin@cloudflight.io",
                    surname = "Big",
                    userRole = mockk(),
                    userStatus = UserStatus.ACTIVE
                ),
                uploaded = YEARS_AGO_20,
                description = "dummy description",
            ),
        )

        private fun expectedAttachment(reportId: Long) = ProjectReportProcurementFile(
            id = 658L,
            reportId = reportId,
            createdInThisReport = false,
            name = "filename.ext",
            type = JemsFileType.ProcurementAttachment,
            uploaded = YEARS_AGO_20,
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 989656189L,
            description = "dummy description"
        )
    }

    @MockK
    lateinit var reportProcurementAttachmentRepository: ProjectPartnerReportProcurementAttachmentRepository

    @InjectMockKs
    lateinit var persistence: ProjectPartnerReportProcurementAttachmentPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportProcurementAttachmentRepository)
    }

    @Test
    fun getAttachmentsBeforeAndIncludingReportId() {
        val procurementId = 18L
        val reportId = 118L

        every { reportProcurementAttachmentRepository
            .findTop30ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(
                procurementId = procurementId,
                reportId = reportId,
            )
        } returns listOf(dummyEntity(mockk()))

        assertThat(persistence.getAttachmentsBeforeAndIncludingReportId(procurementId, reportId = reportId))
            .containsExactly(expectedAttachment(reportId))
    }

    @Test
    fun countAttachmentsCreatedUpUntilNow() {
        val procurementId = 18L
        val reportId = 118L

        every { reportProcurementAttachmentRepository
            .countAttachmentsCreatedBeforeIncludingThis(
                procurementId = procurementId,
                reportId = reportId,
            )
        } returns 222L

        assertThat(persistence.countAttachmentsCreatedUpUntilNow(procurementId, reportId = reportId))
            .isEqualTo(222L)
    }
}
