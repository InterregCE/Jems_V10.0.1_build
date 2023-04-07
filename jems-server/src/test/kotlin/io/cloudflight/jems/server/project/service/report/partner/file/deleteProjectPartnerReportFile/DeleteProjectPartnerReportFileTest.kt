package io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteProjectPartnerReportFileTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 420L
    }

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @MockK
    lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService

    @InjectMockKs
    lateinit var interactor: DeleteProjectPartnerReportFile
    @BeforeEach

    fun reset() {
        clearMocks(partnerPersistence)
        clearMocks(filePersistence)
    }

    @Test
    fun delete() {
        val projectId = 96L
        val fileId = 10L
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(partnerId = PARTNER_ID, fileId = fileId) } returns false
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns projectId
        val searchIndexSlot = slot<String>()
        every { filePersistence.existsFile(PARTNER_ID, capture(searchIndexSlot), fileId) } returns true
        every { filePersistence.deleteFile(PARTNER_ID, fileId) } answers { }

        interactor.delete(PARTNER_ID, reportId = 1890L, fileId)
        verify(exactly = 1) { filePersistence.deleteFile(PARTNER_ID, fileId) }
        assertThat(searchIndexSlot.captured).isEqualTo("Project/000096/Report/Partner/000420/PartnerReport/001890/")
    }

    @Test
    fun `delete - not existing`() {
        val projectId = 94L
        val fileId = -1L
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(partnerId = PARTNER_ID, fileId = fileId) } returns false
        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(PARTNER_ID) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns projectId
        every { filePersistence.existsFile(PARTNER_ID, any(), fileId) } returns false
        assertThrows<FileNotFound> { interactor.delete(PARTNER_ID, 5L, -1L) }
        verify(exactly = 0) { filePersistence.deleteFile(any<Long>(), any()) }
    }


    @Test
    fun `delete sensitive file throws error for non gdpr user`() {
        val partnerId = 129L
        val fileId = 784L

        every { sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId) } returns false
        every { reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(
            partnerId, fileId = fileId) } returns true

        assertThrows<SensitiveFileException> {
            interactor.delete(partnerId, 871L, fileId = fileId) }
    }

}
