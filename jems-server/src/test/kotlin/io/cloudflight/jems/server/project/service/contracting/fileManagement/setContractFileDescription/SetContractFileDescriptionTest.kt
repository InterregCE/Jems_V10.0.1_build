package io.cloudflight.jems.server.project.service.contracting.fileManagement.setContractFileDescription

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.FileNotFound
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetContractFileDescriptionTest: UnitTest() {

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var setContractFileDescription: SetContractFileDescription


    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, reportFilePersistence)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        val projectId = 8L
        every {
            reportFilePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(
                projectId,
                200L,
                setOf(ProjectPartnerReportFileType.ContractDoc, ProjectPartnerReportFileType.Contract)
            )
        } returns true
        every { reportFilePersistence.setDescriptionToFile(200L, "new desc") } answers { }

        setContractFileDescription.setContractFileDescription(projectId, 200L, "new desc")
        verify(exactly = 1) { reportFilePersistence.setDescriptionToFile(200L, "new desc") }
    }

    @Test
    fun `setDescription - not existing`() {
        val projectId = 8L
        every {
            reportFilePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(
                projectId,
                -1,
                setOf(ProjectPartnerReportFileType.ContractDoc, ProjectPartnerReportFileType.Contract)
            )
        } returns false
        assertThrows<FileNotFound> { setContractFileDescription.setContractFileDescription(projectId, -1, "new desc") }
    }
}
