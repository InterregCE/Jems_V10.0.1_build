package io.cloudflight.jems.server.project.repository.report.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.file.ProjectPartnerReportProcurementFileEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.repository.report.procurement.attachment.ProjectPartnerReportProcurementAttachmentRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.CapturingSlot
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO
import java.time.ZonedDateTime
import java.util.*

class ProjectReportFilePersistenceProviderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 877L
        private const val PARTNER_ID = 365L
        private const val USER_ID = 270L

        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private const val BUCKET = "custom_bucket"

        private fun file(id: Long, name: String = "file.txt", filePathFull: String = "path/to/file.txt") = ReportProjectFileEntity(
            id = id,
            projectId = 6666L,
            partnerId = PARTNER_ID,
            path = "",
            minioBucket = BUCKET,
            minioLocation = filePathFull,
            name = name,
            type = ProjectPartnerReportFileType.Activity,
            size = 45L,
            user = mockk(),
            uploaded = LAST_WEEK,
            description = "desc",
        )

        private fun activity(id: Long, attachment: ReportProjectFileEntity?) = ProjectPartnerReportWorkPackageActivityEntity(
            id = id,
            workPackageEntity = mockk(),
            number = 1,
            activityId = null,
            attachment = attachment,
        )

        private fun deliverable(id: Long, attachment: ReportProjectFileEntity?) = ProjectPartnerReportWorkPackageActivityDeliverableEntity(
            id = id,
            activityEntity = mockk(),
            number = 1,
            deliverableId = null,
            contribution = true,
            evidence = false,
            attachment = attachment,
        )

        private fun output(id: Long, attachment: ReportProjectFileEntity?) = ProjectPartnerReportWorkPackageOutputEntity(
            id = id,
            workPackageEntity = mockk(),
            number = 1,
            contribution = true,
            evidence = false,
            attachment = attachment,
        )

        private fun contribution(id: Long, attachment: ReportProjectFileEntity?) = ProjectPartnerReportContributionEntity(
            id = id,
            reportEntity = mockk(),
            sourceOfContribution = "source text",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = 200L,
            historyIdentifier = UUID.randomUUID(),
            createdInThisReport = true,
            amount = ONE,
            previouslyReported = ONE,
            currentlyReported = ONE,
            attachment = attachment,
        )

        private fun expenditure(id: Long, attachment: ReportProjectFileEntity?) = PartnerReportExpenditureCostEntity(
            id = id,
            partnerReport = mockk(),
            reportLumpSum = null,
            reportUnitCost = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            investmentId = 1L,
            procurementId = 1L,
            internalReferenceNumber = "internalReferenceNumber",
            invoiceNumber = "invoiceNumber",
            invoiceDate = LAST_WEEK.toLocalDate(),
            dateOfPayment = LAST_WEEK.plusWeeks(1).toLocalDate(),
            totalValueInvoice = ONE,
            vat = ONE,
            numberOfUnits = ONE,
            pricePerUnit = ZERO,
            declaredAmount = ONE,
            currencyCode = "currencyCode",
            currencyConversionRate = ONE,
            declaredAmountAfterSubmission = ONE,
            attachment = attachment,
        )

        private fun fileCreate(name: String = "new_file.txt", type: ProjectPartnerReportFileType) = ProjectReportFileCreate(
            projectId = 6666L,
            partnerId = PARTNER_ID,
            name = name,
            path = "our/indexed/path/",
            type = type,
            size = 45L,
            content = mockk(),
            userId = USER_ID,
        )

        private val dummyReportFile = ProjectReportFile(
            id = 478L,
            name = "attachment.pdf",
            type = ProjectPartnerReportFileType.Contribution,
            uploaded = LAST_WEEK,
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            description = "desc",
        )

        private val dummyReportFileEntity = ReportProjectFileEntity(
            id = 478L,
            projectId = 4L,
            partnerId = 5L,
            path = "",
            minioBucket = "minioBucket",
            minioLocation = "",
            name = "attachment.pdf",
            type = ProjectPartnerReportFileType.Contribution,
            size = 47889L,
            user = UserEntity(id = 45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big",
                password = "##", userRole = mockk(), userStatus = mockk()),
            uploaded = LAST_WEEK,
            description = "desc",
        )

    }

    @MockK
    lateinit var reportFileRepository: ProjectReportFileRepository

    @MockK
    lateinit var minioStorage: MinioStorage

    @MockK
    lateinit var workPlanActivityRepository: ProjectPartnerReportWorkPackageActivityRepository

    @MockK
    lateinit var workPlanActivityDeliverableRepository: ProjectPartnerReportWorkPackageActivityDeliverableRepository

    @MockK
    lateinit var workPlanOutputRepository: ProjectPartnerReportWorkPackageOutputRepository

    @MockK
    lateinit var contributionRepository: ProjectPartnerReportContributionRepository

    @MockK
    lateinit var expenditureRepository: ProjectPartnerReportExpenditureRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var reportProcurementAttachmentRepository: ProjectPartnerReportProcurementAttachmentRepository

    @MockK
    lateinit var procurementRepository: ProjectPartnerReportProcurementRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(minioStorage)
        clearMocks(reportFileRepository)
    }

    @Test
    fun existsFileByLocation() {
        every { reportFileRepository.existsByPathAndName(path = "Project/Report/Partner/", name = "test.xlsx") } returns false
        assertThat(persistence.existsFile(exactPath = "Project/Report/Partner/", fileName = "test.xlsx")).isFalse
    }

    @Test
    fun existsFile() {
        every { reportFileRepository.existsByPartnerIdAndPathPrefixAndId(
            partnerId = PARTNER_ID,
            pathPrefix = "Project/45/Report/21",
            id = 14L,
        ) } returns true
        assertThat(persistence.existsFile(PARTNER_ID, "Project/45/Report/21", fileId = 14L)).isTrue
    }

    @Test
    fun existsFileByProjectIdAndFileIdAndFileTypeIn() {
        every { reportFileRepository.existsByProjectIdAndIdAndTypeIn(
            projectId = PROJECT_ID,
            fileId = 15L,
            fileTypes = setOf(ProjectPartnerReportFileType.ContractInternal),
        ) } returns true
        assertThat(
            persistence.existsFileByProjectIdAndFileIdAndFileTypeIn(PROJECT_ID, fileId = 15L, setOf(ProjectPartnerReportFileType.ContractInternal))
        ).isTrue()
    }

    @Test
    fun getFileAuthor() {
        val reportFile = mockk<ReportProjectFileEntity>()

        val user = mockk<UserEntity>()
        every { user.id } returns USER_ID
        every { user.email } returns "email 270"
        every { user.name } returns "name 270"
        every { user.surname } returns "surname 270"

        every { reportFile.user } returns user
        every { reportFileRepository.findByPartnerIdAndPathPrefixAndId(
            partnerId = PARTNER_ID,
            pathPrefix = "prefix",
            id = 16L,
        ) } returns reportFile
        assertThat(
            persistence.getFileAuthor(PARTNER_ID, "prefix", fileId = 16L)
        ).isEqualTo(UserSimple(USER_ID, "email 270", name = "name 270", "surname 270"))
    }

    @Test
    fun `getFileAuthor - not existing file`() {
        every { reportFileRepository.findByPartnerIdAndPathPrefixAndId(
            partnerId = PARTNER_ID,
            pathPrefix = "prefix",
            id = 16L,
        ) } returns null
        assertThat(
            persistence.getFileAuthor(PARTNER_ID, "prefix", fileId = 16L)
        ).isNull()
    }

    @Test
    fun downloadFile() {
        val filePathFull = "sample/path/to/file.txt"
        every { reportFileRepository.findByPartnerIdAndId(PARTNER_ID, fileId = 17L) } returns
            file(id = 17L, name = "file.txt", filePathFull = filePathFull)
        every { minioStorage.getFile(BUCKET, filePathFull) } returns ByteArray(5)

        assertThat(persistence.downloadFile(PARTNER_ID, fileId = 17L))
            .usingRecursiveComparison()
            .isEqualTo(Pair("file.txt", ByteArray(5)))
    }

    @Test
    fun `downloadFile - not existing`() {
        every { reportFileRepository.findByPartnerIdAndId(PARTNER_ID, fileId = -1L) } returns null
        assertThat(persistence.downloadFile(PARTNER_ID, fileId = -1L)).isNull()
        verify(exactly = 0) { minioStorage.getFile(any(), any()) }
    }

    @Test
    fun `downloadFile - by type`() {
        val filePathFull = "sample/path/to/file.txt"
        every { reportFileRepository.findByTypeAndId(ProjectPartnerReportFileType.PaymentAttachment, fileId = 20L) } returns
            file(id = 20L, name = "file.txt", filePathFull = filePathFull)
        every { minioStorage.getFile(BUCKET, filePathFull) } returns ByteArray(5)

        assertThat(persistence.downloadFile(ProjectPartnerReportFileType.PaymentAttachment, fileId = 20L))
            .usingRecursiveComparison()
            .isEqualTo(Pair("file.txt", ByteArray(5)))
    }

    @Test
    fun `downloadFile - by type - not existing`() {
        every { reportFileRepository.findByTypeAndId(ProjectPartnerReportFileType.PaymentAttachment, fileId = -1L) } returns null
        assertThat(persistence.downloadFile(ProjectPartnerReportFileType.PaymentAttachment, fileId = -1L)).isNull()
        verify(exactly = 0) { minioStorage.getFile(any(), any()) }
    }

    @Test
    fun deleteFile() {
        val filePathFull = "sample/path/to/file-to-delete.txt"
        val fileToDelete = file(id = 20L, name = "file-to-delete.txt", filePathFull = filePathFull)
        every { reportFileRepository.findByTypeAndId(ProjectPartnerReportFileType.PaymentAttachment, fileId = 20L) } returns fileToDelete
        every { minioStorage.deleteFile(BUCKET, filePathFull) } answers { }
        every { reportFileRepository.delete(fileToDelete) } answers { }

        persistence.deleteFile(ProjectPartnerReportFileType.PaymentAttachment, fileId = 20L)

        verify(exactly = 1) { minioStorage.deleteFile(BUCKET, filePathFull) }
        verify(exactly = 1) { reportFileRepository.delete(fileToDelete) }
    }

    @Test
    fun `deleteFile - not existing`() {
        every { reportFileRepository.findByPartnerIdAndId(PARTNER_ID, fileId = -1L) } returns null
        persistence.deleteFile(PARTNER_ID, fileId = -1L)

        verify(exactly = 0) { minioStorage.deleteFile(any(), any()) }
        verify(exactly = 0) { reportFileRepository.delete(any()) }
    }

    @Test
    fun `deleteFile - by type`() {
        val filePathFull = "sample/path/to/file-to-delete.txt"
        val fileToDelete = file(id = 21L, name = "file-to-delete.txt", filePathFull = filePathFull)
        every { reportFileRepository.findByTypeAndId(ProjectPartnerReportFileType.PaymentAttachment, fileId = 21L) } returns fileToDelete
        every { minioStorage.deleteFile(BUCKET, filePathFull) } answers { }
        every { reportFileRepository.delete(fileToDelete) } answers { }

        persistence.deleteFile(ProjectPartnerReportFileType.PaymentAttachment, fileId = 21L)

        verify(exactly = 1) { minioStorage.deleteFile(BUCKET, filePathFull) }
        verify(exactly = 1) { reportFileRepository.delete(fileToDelete) }
    }

    @Test
    fun `deleteFile - by type - not existing`() {
        every { reportFileRepository.findByTypeAndId(ProjectPartnerReportFileType.PaymentAttachment, fileId = -1L) } returns null
        persistence.deleteFile(ProjectPartnerReportFileType.PaymentAttachment, fileId = -1L)

        verify(exactly = 0) { minioStorage.deleteFile(any(), any()) }
        verify(exactly = 0) { reportFileRepository.delete(any()) }
    }

    @Test
    fun setDescription() {
        val filePathFull = "sample/path/to/file-with-desc.txt"
        val fileToUpdate = file(id = 20L, name = "file-with-desc.txt", filePathFull = filePathFull)
        every { reportFileRepository.findById( 20L) } returns Optional.of(fileToUpdate)

        persistence.setDescriptionToFile(fileId = 20L, "description new")
        assertThat(fileToUpdate.description).isEqualTo("description new")
    }

    @Test
    fun `setDescription - not existing`() {
        every { reportFileRepository.findById(-1L) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { persistence.setDescriptionToFile(fileId = -1L, "") }
    }

    @Test
    fun updatePartnerReportActivityAttachment() {
        val filePathMinio = slot<String>()
        val fileEntity = slot<ReportProjectFileEntity>()

        val oldFile = mockk<ReportProjectFileEntity>()

        val activity = activity(id = 80L, attachment = oldFile)
        every { workPlanActivityRepository.findById(80L) } returns Optional.of(activity)

        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.Activity)
        mockFileDeletionAndSaving(oldFile, filePathMinio, fileEntity)

        assertThat(persistence.updatePartnerReportActivityAttachment(80L, file = fileCreate).name)
            .isEqualTo("new_file.txt")

        assertFile(filePathMinio.captured, fileEntity.captured)
    }

    @Test
    fun updatePartnerReportDeliverableAttachment() {
        val filePathMinio = slot<String>()
        val fileEntity = slot<ReportProjectFileEntity>()

        val oldFile = mockk<ReportProjectFileEntity>()

        val deliverable = deliverable(id = 90L, attachment = oldFile)
        every { workPlanActivityDeliverableRepository.findById(90L) } returns Optional.of(deliverable)

        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.Deliverable)
        mockFileDeletionAndSaving(oldFile, filePathMinio, fileEntity)

        assertThat(persistence.updatePartnerReportDeliverableAttachment(90L, file = fileCreate).name)
            .isEqualTo("new_file.txt")

        assertFile(filePathMinio.captured, fileEntity.captured)
        assertThat(fileEntity.captured.type).isEqualTo(ProjectPartnerReportFileType.Deliverable)
    }

    @Test
    fun updatePartnerReportContributionAttachment() {
        val filePathMinio = slot<String>()
        val fileEntity = slot<ReportProjectFileEntity>()

        val oldFile = mockk<ReportProjectFileEntity>()

        val contribution = contribution(id = 88L, attachment = oldFile)
        every { contributionRepository.findById(50L) } returns Optional.of(contribution)

        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.Contribution)
        mockFileDeletionAndSaving(oldFile, filePathMinio, fileEntity)

        assertThat(persistence.updatePartnerReportContributionAttachment(50L, file = fileCreate).name)
            .isEqualTo("new_file.txt")

        assertFile(filePathMinio.captured, fileEntity.captured)
        assertThat(fileEntity.captured.type).isEqualTo(ProjectPartnerReportFileType.Contribution)
    }

    @Test
    fun updatePartnerReportOutputAttachment() {
        val filePathMinio = slot<String>()
        val fileEntity = slot<ReportProjectFileEntity>()

        val oldFile = mockk<ReportProjectFileEntity>()

        val output = output(id = 70L, attachment = oldFile)
        every { workPlanOutputRepository.findById(70L) } returns Optional.of(output)

        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.Output)
        mockFileDeletionAndSaving(oldFile, filePathMinio, fileEntity)

        assertThat(persistence.updatePartnerReportOutputAttachment(70L, file = fileCreate).name)
            .isEqualTo("new_file.txt")

        assertFile(filePathMinio.captured, fileEntity.captured)
        assertThat(fileEntity.captured.type).isEqualTo(ProjectPartnerReportFileType.Output)
    }

    @Test
    fun updatePartnerReportExpenditureAttachment() {
        val filePathMinio = slot<String>()
        val fileEntity = slot<ReportProjectFileEntity>()

        val oldFile = mockk<ReportProjectFileEntity>()

        val expenditure = expenditure(id = 90L, attachment = oldFile)
        every { expenditureRepository.findById(40L) } returns Optional.of(expenditure)

        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.Expenditure)
        mockFileDeletionAndSaving(oldFile, filePathMinio, fileEntity)

        assertThat(persistence.updatePartnerReportExpenditureAttachment(40L, file = fileCreate).name)
            .isEqualTo("new_file.txt")

        assertFile(filePathMinio.captured, fileEntity.captured)
        assertThat(fileEntity.captured.type).isEqualTo(ProjectPartnerReportFileType.Expenditure)
    }

    @Test
    fun addPartnerReportProcurementAttachment() {
        val filePathMinio = slot<String>()
        val fileEntity = slot<ReportProjectFileEntity>()
        val oldFile = mockk<ReportProjectFileEntity>() // this is not used here
        mockFileDeletionAndSaving(oldFile, filePathMinio, fileEntity)

        val procurementId = 500L
        val procurement = mockk<ProjectPartnerReportProcurementEntity>()
        every { procurementRepository.getById(procurementId) } returns procurement
        val procurementFile = slot<ProjectPartnerReportProcurementFileEntity>()
        every { reportProcurementAttachmentRepository.save(capture(procurementFile)) } returnsArgument 0

        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.ProcurementAttachment)
        assertThat(persistence
            .addPartnerReportProcurementAttachment(reportId = 48L, file = fileCreate, procurementId = procurementId).name
        ).isEqualTo("new_file.txt")

        assertFile(filePathMinio.captured, fileEntity.captured)
        assertThat(fileEntity.captured.type).isEqualTo(ProjectPartnerReportFileType.ProcurementAttachment)

        assertThat(procurementFile.captured.procurement).isEqualTo(procurement)
        assertThat(procurementFile.captured.file).isEqualTo(fileEntity.captured)
        assertThat(procurementFile.captured.createdInReportId).isEqualTo(48L)
    }

    private fun mockFileDeletionAndSaving(
        oldFile: ReportProjectFileEntity,
        filePathMinio: CapturingSlot<String>,
        fileEntity: CapturingSlot<ReportProjectFileEntity>,
    ) {
        every { oldFile.minioBucket } returns "bucket"
        every { oldFile.minioLocation } returns "remove/me.pdf"

        every { minioStorage.deleteFile("bucket", "remove/me.pdf") } answers { }
        every { reportFileRepository.delete(oldFile) } answers { }

        every { minioStorage.saveFile("project-report", capture(filePathMinio), any(), any(), true) } answers { }
        every { userRepository.getById(270) } returns mockk()
        every { reportFileRepository.save(capture(fileEntity)) } returnsArgument 0
    }

    private fun assertFile(filePathMinio: String, fileEntity: ReportProjectFileEntity) {
        assertThat(filePathMinio).isEqualTo("our/indexed/path/new_file.txt")
        assertThat(fileEntity.partnerId).isEqualTo(PARTNER_ID)
        assertThat(fileEntity.path).isEqualTo("our/indexed/path/")
        assertThat(fileEntity.minioBucket).isEqualTo("project-report")
        assertThat(fileEntity.minioLocation).isEqualTo("our/indexed/path/new_file.txt")
        assertThat(fileEntity.name).isEqualTo("new_file.txt")
    }

    @Test
    fun listAttachments() {
        val filterSubtypes = setOf(ProjectPartnerReportFileType.Activity)
        val filterUserIds = setOf(45L, 46L, 47L)

        every { reportFileRepository.filterAttachment(
            pageable = Pageable.unpaged(),
            indexPrefix = "indexPrefix",
            filterSubtypes = filterSubtypes,
            filterUserIds = filterUserIds,
        ) } returns PageImpl(listOf(dummyReportFileEntity))

        assertThat(persistence.listAttachments(Pageable.unpaged(), "indexPrefix", filterSubtypes, filterUserIds).content)
            .containsExactly(dummyReportFile)
    }

    @Test
    fun addAttachmentToPartnerReport() {
        val filePathMinio = slot<String>()
        val fileEntity = slot<ReportProjectFileEntity>()

        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.PartnerReport).copy(name = "new_file_to_partner.txt")

        every { minioStorage.saveFile("project-report", capture(filePathMinio), any(), any(), true) } answers { }
        every { userRepository.getById(270) } returns mockk()
        every { reportFileRepository.save(capture(fileEntity)) } returnsArgument 0

        assertThat(persistence.addAttachmentToPartnerReport(file = fileCreate).name)
            .isEqualTo("new_file_to_partner.txt")

        assertThat(filePathMinio.captured).isEqualTo("our/indexed/path/new_file_to_partner.txt")
        assertThat(fileEntity.captured.partnerId).isEqualTo(PARTNER_ID)
        assertThat(fileEntity.captured.path).isEqualTo("our/indexed/path/")
        assertThat(fileEntity.captured.minioBucket).isEqualTo("project-report")
        assertThat(fileEntity.captured.minioLocation).isEqualTo("our/indexed/path/new_file_to_partner.txt")
        assertThat(fileEntity.captured.name).isEqualTo("new_file_to_partner.txt")
        assertThat(fileEntity.captured.type).isEqualTo(ProjectPartnerReportFileType.PartnerReport)
        assertThat(fileEntity.captured.size).isEqualTo(45L)
        assertThat(fileEntity.captured.description).isEmpty()
    }

}
