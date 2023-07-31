package io.cloudflight.jems.server.project.service.report.project.verification.file.list

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ListProjectReportVerificationFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 71L
        const val REPORT_ID = 73L
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var projectReportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var interactor: ListProjectReportVerificationFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, projectReportPersistence)
    }

    @Test
    fun list() {
        val path = VerificationDocument.generatePath(PROJECT_ID, REPORT_ID)
        val fileList = mockk<Page<JemsFile>>()

        every { projectReportPersistence.exists(PROJECT_ID, REPORT_ID) } returns true
        every { filePersistence.listAttachments(Pageable.unpaged(), path, setOf(VerificationDocument), setOf()) } returns fileList

        assertThat(interactor.list(PROJECT_ID, REPORT_ID, Pageable.unpaged()))
            .isEqualTo(fileList)
    }

    @Test
    fun notFound() {
        every { projectReportPersistence.exists(PROJECT_ID, REPORT_ID) } returns false

        assertThrows<FileNotFound> { interactor.list(PROJECT_ID, REPORT_ID, Pageable.unpaged()) }
    }
}
