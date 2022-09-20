package io.cloudflight.jems.server.project.service.file.set_project_file_description

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.repository.ProjectNotFoundException
import io.cloudflight.jems.server.project.repository.file.ProjectFileNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.utils.FILE_ID
import io.cloudflight.jems.server.utils.FILE_NAME
import io.cloudflight.jems.server.utils.PROJECT_ID
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.utils.USER_ID
import io.cloudflight.jems.server.utils.fileMetadata
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class SetProjectFileDescriptionTest : UnitTest() {

    private val inputErrorMap = mapOf("error" to I18nMessage("error.key"))

    @MockK
    lateinit var filePersistence: ProjectFilePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var setProjectFileDescription: SetProjectFileDescription


    @BeforeEach
    fun reset() {
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(
            inputErrorMap
        )
    }

    @Test
    fun `should throw AppInputValidationException when description length is not valid`() {
        val descriptionSlot = slot<String>()
        val validLength = 250
        val fieldName = "description"
        every { generalValidator.maxLength(capture(descriptionSlot), validLength, fieldName) } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            setProjectFileDescription.setDescription(PROJECT_ID, FILE_ID, getStringOfLength(251))
        }
        verify(exactly = 1) { generalValidator.maxLength(descriptionSlot.captured, validLength, fieldName) }

    }

    @Test
    fun `should throw ProjectNotFoundException when project does not exist`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } throws ProjectNotFoundException()
        assertThrows<ProjectNotFoundException> {
            setProjectFileDescription.setDescription(PROJECT_ID, FILE_ID, "description")
        }
    }

    @Test
    fun `should throw ProjectFileNotFoundException when project file metadata does not exist`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { filePersistence.getFileMetadata(FILE_ID) } throws ProjectFileNotFoundException()
        assertThrows<ProjectFileNotFoundException> {
            setProjectFileDescription.setDescription(PROJECT_ID, FILE_ID, "a")
        }
    }

    @Test
    fun `should set project file description when there is no problem`() {
        val newDescription = "latest version"
        val fileMetadata = fileMetadata(description = newDescription)
        val auditSlot = slot<AuditCandidateEvent>()
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { filePersistence.getFileMetadata(FILE_ID) } returns fileMetadata
        every { filePersistence.setFileDescription(FILE_ID, newDescription) } returns fileMetadata
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThat(
            setProjectFileDescription.setDescription(PROJECT_ID, FILE_ID, newDescription)
        ).isEqualTo(fileMetadata)

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_FILE_DESCRIPTION_CHANGED)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "description of document $FILE_NAME in project application $PROJECT_ID has changed from `${fileMetadata.description}` to `$newDescription` by $USER_ID"
        )

    }
}
