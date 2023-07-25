package io.cloudflight.jems.server.project.service.contracting.fileManagement.listPartnerFiles

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZoneId
import java.time.ZonedDateTime

internal class ListContractingPartnerFilesTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 10L
        private const val PARTNER_ID = 20L

        private val projectReportFile = JemsFile(
            id = 1L,
            name = "test",
            type = JemsFileType.ContractPartnerDoc,
            uploaded = ZonedDateTime.of(2020,1,30,15,10,10,10, ZoneId.systemDefault()),
            author = UserSimple(45L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 6281245L,
            description = "Description",
            indexedPath = ""
        )
    }

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: ListContractingPartnerFiles

    @BeforeEach
    fun setup() {
        clearMocks(partnerPersistence)
        clearMocks(filePersistence)
    }

    @Test
    fun `list partner files`() {
        val filters = setOf(JemsFileType.ContractPartnerDoc)
        val indexPrefix = slot<String>()
        val result = PageImpl(listOf(projectReportFile))
        every { filePersistence.listAttachments(Pageable.unpaged(), capture(indexPrefix), filters, any()) } returns PageImpl(listOf(projectReportFile))
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID

        val searchRequest = ProjectContractingFileSearchRequest(
            treeNode = JemsFileType.ContractPartnerDoc,
            filterSubtypes = filters,
        )
        Assertions.assertThat(interactor.listPartner(PARTNER_ID, Pageable.unpaged(), searchRequest)).isEqualTo(result)
        Assertions.assertThat(indexPrefix.captured).isEqualTo("Project/000010/Contracting/ContractPartner/ContractPartnerDoc/000020/")
    }
}
