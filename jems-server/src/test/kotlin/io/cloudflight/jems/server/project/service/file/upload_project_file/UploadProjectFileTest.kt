package io.cloudflight.jems.server.project.service.file.upload_project_file

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.repository.ProjectNotFoundException
import io.cloudflight.jems.server.project.repository.file.FileNameAlreadyExistsException
import io.cloudflight.jems.server.project.repository.partner.PartnerNotFoundInProjectException
import io.cloudflight.jems.server.project.repository.workpackage.InvestmentNotFoundInProjectException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.utils.FILE_ID
import io.cloudflight.jems.server.utils.FILE_NAME
import io.cloudflight.jems.server.utils.INVESTMENT_ID
import io.cloudflight.jems.server.utils.PARTNER_ID
import io.cloudflight.jems.server.utils.PROJECT_ID
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.utils.USER_ID
import io.cloudflight.jems.server.utils.currentUser
import io.cloudflight.jems.server.utils.fileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
import io.cloudflight.jems.server.utils.projectFile
import io.cloudflight.jems.server.utils.projectFileCategory
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.user.repository.user.UserNotFound
import io.cloudflight.jems.server.user.service.UserPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class UploadProjectFileTest : UnitTest() {

    @MockK
    lateinit var filePersistence: ProjectFilePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var workPackagePersistence: WorkPackagePersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var uploadProjectFile: UploadProjectFile

    @BeforeAll
    fun setup() {
        every { securityService.currentUser } returns currentUser
    }

    @TestFactory
    fun `should throw UploadInCategoryIsNotAllowedExceptions when uploading to the provided category is not allowed`() =
        listOf(
            projectFileCategory(ProjectFileCategoryType.ALL, null),
            projectFileCategory(ProjectFileCategoryType.PARTNER, null),
            projectFileCategory(ProjectFileCategoryType.INVESTMENT, null),
        ).map { projectFileCategory ->
            DynamicTest.dynamicTest(
                "should throw UploadInCategoryIsNotAllowedExceptions when uploading file to category $projectFileCategory"
            ) {
                assertThrows<UploadInCategoryIsNotAllowedExceptions> {
                    uploadProjectFile.upload(PROJECT_ID, projectFileCategory, projectFile)
                }
            }
        }

    @Test
    fun `should throw ProjectNotFoundException when project does not exist`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } throws ProjectNotFoundException()
        assertThrows<ProjectNotFoundException> {
            uploadProjectFile.upload(PROJECT_ID, projectFileCategory(), projectFile)
        }
    }

    @Test
    fun `should throw UserNotFound when user does not exist`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { userPersistence.throwIfNotExists(USER_ID) } throws UserNotFound()
        assertThrows<UserNotFound> {
            uploadProjectFile.upload(PROJECT_ID, projectFileCategory(), projectFile)
        }
    }

    @Test
    fun `should throw PartnerNotFoundInProjectException when uploading file for a partner that does not exist in the project`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { userPersistence.throwIfNotExists(USER_ID) } returns Unit
        every {
            partnerPersistence.throwIfNotExistsInProject(
                PROJECT_ID,
                PARTNER_ID
            )
        } throws PartnerNotFoundInProjectException(
            PROJECT_ID, PARTNER_ID
        )
        assertThrows<PartnerNotFoundInProjectException> {
            uploadProjectFile.upload(PROJECT_ID, projectFileCategory(categoryId = PARTNER_ID), projectFile)
        }
    }

    @Test
    fun `should throw InvestmentNotFoundInProjectException when uploading file for an investment that does not exist in the project`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { userPersistence.throwIfNotExists(USER_ID) } returns Unit
        every {
            workPackagePersistence.throwIfInvestmentNotExistsInProject(PROJECT_ID, INVESTMENT_ID)
        } throws InvestmentNotFoundInProjectException(PROJECT_ID, INVESTMENT_ID)
        assertThrows<InvestmentNotFoundInProjectException> {
            uploadProjectFile.upload(
                PROJECT_ID,
                projectFileCategory(categoryType = ProjectFileCategoryType.INVESTMENT, categoryId = INVESTMENT_ID),
                projectFile
            )
        }
    }

    @Test
    fun `should throw FileNameAlreadyExistsException when uploading file with duplicate name in ASSESSMENT category for a particular project`() {
        val category = projectFileCategory(categoryType = ProjectFileCategoryType.ASSESSMENT, categoryId = null)
        val auditSlot = slot<AuditCandidateEvent>()
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { userPersistence.throwIfNotExists(USER_ID) } returns Unit
        every {
            filePersistence.throwIfFileNameExistsInCategory(PROJECT_ID, FILE_NAME, category)
        } throws FileNameAlreadyExistsException()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThrows<FileNameAlreadyExistsException> {
            uploadProjectFile.upload(PROJECT_ID, category, projectFile)
        }
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_FILE_UPLOAD_FAILED)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "FAILED upload of document $FILE_NAME to project application $PROJECT_ID by $USER_ID"
        )
    }


    @Test
    fun `should save file and its metadata when there is no problem`() {
        val category = projectFileCategory(ProjectFileCategoryType.PARTNER, categoryId = PARTNER_ID)
        val fileMetadata = fileMetadata()
        val auditSlot = slot<AuditCandidateEvent>()
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { partnerPersistence.throwIfNotExistsInProject(PROJECT_ID, PARTNER_ID) } returns Unit
        every { userPersistence.throwIfNotExists(USER_ID) } returns Unit
        every { filePersistence.throwIfFileNameExistsInCategory(PROJECT_ID, FILE_NAME, category) } returns Unit
        every {
            filePersistence.saveFileMetadata(PROJECT_ID, USER_ID, projectFile, category)
        } returns fileMetadata
        every { filePersistence.saveFile(PROJECT_ID, FILE_ID, USER_ID, projectFile) } returns Unit
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        uploadProjectFile.upload(PROJECT_ID, category, projectFile)

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_FILE_UPLOADED_SUCCESSFULLY)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "document $FILE_NAME uploaded to project application $PROJECT_ID for Partner ${category.id} by $USER_ID"
        )
    }
}
