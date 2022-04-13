package io.cloudflight.jems.server.project.repository.report.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
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
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.time.ZonedDateTime
import java.util.*

class ProjectReportFilePersistenceProviderTest : UnitTest() {

    companion object {
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

        private fun procurement(id: Long, attachment: ReportProjectFileEntity?) = ProjectPartnerReportProcurementEntity(
            id = id,
            reportEntity = mockk(),
            contractId = "contractId",
            contractAmount = ONE,
            currencyCode = "HRK",
            supplierName = "supplierName",
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
    lateinit var procurementRepository: ProjectPartnerReportProcurementRepository

    @MockK
    lateinit var contributionRepository: ProjectPartnerReportContributionRepository

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(minioStorage)
        clearMocks(reportFileRepository)
    }

    @Test
    fun existsFile() {
        every { reportFileRepository.existsByPartnerIdAndId(PARTNER_ID, fileId = 14L) } returns true
        assertThat(persistence.existsFile(PARTNER_ID, fileId = 14L)).isTrue
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
    fun deleteFile() {
        val filePathFull = "sample/path/to/file-to-delete.txt"
        val fileToDelete = file(id = 20L, name = "file-to-delete.txt", filePathFull = filePathFull)
        every { reportFileRepository.findByPartnerIdAndId(PARTNER_ID, fileId = 20L) } returns fileToDelete
        every { minioStorage.deleteFile(BUCKET, filePathFull) } answers { }
        every { reportFileRepository.delete(fileToDelete) } answers { }

        persistence.deleteFile(PARTNER_ID, fileId = 20L)

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
    fun updatePartnerReportProcurementAttachment() {
        val filePathMinio = slot<String>()
        val fileEntity = slot<ReportProjectFileEntity>()

        val oldFile = mockk<ReportProjectFileEntity>()

        val procurement = procurement(id = 90L, attachment = oldFile)
        every { procurementRepository.findById(40L) } returns Optional.of(procurement)

        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.Procurement)
        mockFileDeletionAndSaving(oldFile, filePathMinio, fileEntity)

        assertThat(persistence.updatePartnerReportProcurementAttachment(40L, file = fileCreate).name)
            .isEqualTo("new_file.txt")

        assertFile(filePathMinio.captured, fileEntity.captured)
        assertThat(fileEntity.captured.type).isEqualTo(ProjectPartnerReportFileType.Procurement)
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

}
