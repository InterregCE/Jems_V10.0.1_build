package io.cloudflight.jems.server.project.service.sharedFolder.description

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.sharedFolderFile.description.FileNotFound
import io.cloudflight.jems.server.project.service.sharedFolderFile.description.SetDescriptionToSharedFolderFile
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SetDescriptionToSharedFolderFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 234L
        private const val FILE_ID = 567L
    }

    @MockK
    private lateinit var generalValidator: GeneralValidatorService

    @MockK
    private lateinit var filePersistence: JemsFilePersistence

    @MockK
    private lateinit var fileService: JemsProjectFileService

    @InjectMockKs
    private lateinit var interactor: SetDescriptionToSharedFolderFile

    @BeforeEach
    fun setUp() {
        clearMocks(generalValidator, filePersistence, fileService)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
                AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), 250, "description") } returns emptyMap()
    }

    @Test
    fun set() {
        every { filePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(PROJECT_ID, FILE_ID, setOf(JemsFileType.SharedFolder)) } returns true
        every { fileService.setDescription(FILE_ID, "updated desc") } answers { }

        interactor.set(PROJECT_ID, FILE_ID, "updated desc")
        verify(exactly = 1) { fileService.setDescription(FILE_ID, "updated desc") }
    }

    @Test
    fun `should throw FileNotFound when said file does not exist`() {
        every { filePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(PROJECT_ID, FILE_ID, setOf(JemsFileType.SharedFolder)) } returns false
        assertThrows<FileNotFound> { interactor.set(PROJECT_ID, FILE_ID, "test") }
    }
}
