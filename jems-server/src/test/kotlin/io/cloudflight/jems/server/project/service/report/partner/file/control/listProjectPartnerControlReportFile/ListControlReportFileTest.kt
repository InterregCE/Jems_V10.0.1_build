package io.cloudflight.jems.server.project.service.report.partner.file.control.listProjectPartnerControlReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType.ControlDocument
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ListControlReportFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 644L
        private const val PARTNER_ID = 224L
    }

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: ListControlReportFile

    @BeforeEach
    fun reset() {
        clearMocks(partnerPersistence)
        clearMocks(filePersistence)
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
    }

    @Test
    fun list() {
        val reportFile = mockk<JemsFile>()
        every { filePersistence.listAttachments(
            pageable = any(),
            indexPrefix = "Project/000644/Report/Partner/000224/PartnerControlReport/000004/",
            filterSubtypes = setOf(ControlDocument),
            filterUserIds = emptySet(),
        ) } returns PageImpl(listOf(reportFile))

        assertThat(interactor.list(PARTNER_ID, 4L, Pageable.unpaged()).content)
            .containsExactly(reportFile)
    }

}
