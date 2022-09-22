package io.cloudflight.jems.server.project.service.contracting.fileManagement.setInternalFileDescriptionTest

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.contracting.fileManagement.setInternalFileDescription.SetInternalFileDescription
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

class SetInternalFileDescriptionTest: UnitTest() {



    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var setInternalFileDescription: SetInternalFileDescription


    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, reportFilePersistence)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }


    @Test
    fun setDescriptionInternal() {
        val projectId = 8L
        every {
            reportFilePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(
                projectId,
                200L,
                setOf(ProjectPartnerReportFileType.ContractInternal)
            )
        } returns true
        every { reportFilePersistence.setDescriptionToFile(200L, "new desc") } answers { }

        setInternalFileDescription.setInternalFileDescription(projectId, 200L, "new desc")
        verify(exactly = 1) { reportFilePersistence.setDescriptionToFile(200L, "new desc") }
    }

    @Test
    fun `setDescriptionInternal - not existing`() {
        val projectId = 8L
        every {
            reportFilePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(
                projectId,
                -1,
                setOf(ProjectPartnerReportFileType.ContractInternal)
            )
        } returns false
        assertThrows<FileNotFound> { setInternalFileDescription.setInternalFileDescription(projectId, -1, "new desc") }
    }
}
