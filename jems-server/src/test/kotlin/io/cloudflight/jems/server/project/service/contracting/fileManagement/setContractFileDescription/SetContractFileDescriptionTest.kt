package io.cloudflight.jems.server.project.service.contracting.fileManagement.setContractFileDescription

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
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
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var fileService: JemsProjectFileService

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @MockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var setContractFileDescription: SetContractFileDescription

    @BeforeEach
    fun setup() {
        clearMocks(generalValidator, filePersistence, fileService)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun setDescription() {
        val projectId = 8L
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, projectId) } returns Unit
        every {
            filePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(
                projectId,
                200L,
                setOf(JemsFileType.ContractDoc, JemsFileType.Contract)
            )
        } returns true
        every { fileService.setDescription(200L, "new desc") } answers { }

        setContractFileDescription.setContractFileDescription(projectId, 200L, "new desc")
        verify(exactly = 1) { fileService.setDescription(200L, "new desc") }
    }

    @Test
    fun `setDescription - does not exist`() {
        val projectId = 8L
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, projectId) } returns Unit
        every {
            filePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(
                projectId,
                -1,
                setOf(JemsFileType.ContractDoc, JemsFileType.Contract)
            )
        } returns false
        assertThrows<FileNotFound> { setContractFileDescription.setContractFileDescription(projectId, -1, "new desc") }
    }

    @Test
    fun `setDescription - section locked`() {
        val projectId = 8L
        val exception = ContractingModificationDeniedException()
        every { validator.validateSectionLock(ProjectContractingSection.ContractsAgreements, projectId) } throws exception

        assertThrows<ContractingModificationDeniedException> {
            setContractFileDescription.setContractFileDescription(projectId, 200L, "new desc")
        }
    }
}
