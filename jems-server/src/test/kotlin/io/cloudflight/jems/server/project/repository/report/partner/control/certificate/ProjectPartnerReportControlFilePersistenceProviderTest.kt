package io.cloudflight.jems.server.project.repository.report.partner.control.certificate

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.entity.report.control.certificate.PartnerReportControlFileEntity
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

class ProjectPartnerReportControlFilePersistenceProviderTest : UnitTest() {
    companion object {
        private const val REPORT_ID = 2L
        private val DATE = ZonedDateTime.now()

        val file = JemsFileMetadataEntity(
            id = 1L,
            partnerId = null,
            projectId = null,
            path = "some path",
            minioBucket = "some-bucket",
            minioLocation = "some location",
            name = "some name",
            type = JemsFileType.ControlDocument,
            size = 2L,
            user = UserEntity(
                id = 1L,
                email = "some@email",
                name = "Smith",
                surname = "Jon",
                userRole = UserRoleEntity(
                    id = 1L,
                    name = "role"
                ),
                password = "some pass",
                userStatus = UserStatus.ACTIVE
            ),
            uploaded = DATE,
            description = "some description"
        )

        val result = PartnerReportControlFileEntity(
            id = 1L,
            reportId = 2L,
            generatedFile = file,
            signedFile = null
        )

        val expectedResult = PartnerReportControlFile(
            id = 1L,
            reportId = 2L,
            generatedFile = JemsFile(
                id = 1L,
                name = "some name",
                type = JemsFileType.ControlDocument,
                uploaded = DATE,
                author = UserSimple(
                    id = 1L,
                    email = "some@email",
                    name = "Smith",
                    surname = "Jon"
                ),
                size = 2L,
                description = "some description"
            ),
            signedFile = null
        )
    }

    @MockK
    lateinit var reportControlFileRepository: ProjectPartnerReportControlFileRepository

    @MockK
    lateinit var fileRepository: JemsProjectFileService

    @InjectMockKs
    lateinit var persistence: ProjectPartnerReportControlFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(fileRepository, reportControlFileRepository)
    }


    @Test
    fun getListOfControlReportFiles() {
        every { reportControlFileRepository.findAllByReportId(REPORT_ID, Pageable.unpaged()) } returns PageImpl(listOf(result))
        Assertions.assertThat(persistence.listReportFiles(REPORT_ID, Pageable.unpaged()).content).containsExactly(expectedResult)
    }
}
