package io.cloudflight.jems.server.project.repository.report.partner.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.entity.report.partner.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.partner.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.partner.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.partner.procurement.ProjectPartnerReportProcurementRepository
import io.cloudflight.jems.server.project.repository.report.partner.procurement.attachment.ProjectPartnerReportProcurementAttachmentRepository
import io.cloudflight.jems.server.project.repository.report.partner.procurement.gdprAttachment.ProjectPartnerReportProcurementGdprAttachmentRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO
import java.time.ZonedDateTime
import java.util.Optional
import java.util.UUID

class ProjectPartnerReportFilePersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 365L
        private const val USER_ID = 270L

        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)

        private fun activity(id: Long, attachment: JemsFileMetadataEntity?) = ProjectPartnerReportWorkPackageActivityEntity(
            id = id,
            workPackageEntity = mockk(),
            number = 1,
            activityId = null,
            attachment = attachment,
            deactivated = false,
        )

        private fun deliverable(id: Long, attachment: JemsFileMetadataEntity?) = ProjectPartnerReportWorkPackageActivityDeliverableEntity(
            id = id,
            activityEntity = mockk(),
            number = 1,
            deliverableId = null,
            contribution = true,
            evidence = false,
            attachment = attachment,
            deactivated = false,
        )

        private fun output(id: Long, attachment: JemsFileMetadataEntity?) = ProjectPartnerReportWorkPackageOutputEntity(
            id = id,
            workPackageEntity = mockk(),
            number = 1,
            contribution = true,
            evidence = false,
            attachment = attachment,
            deactivated = false,
        )

        private fun contribution(id: Long, attachment: JemsFileMetadataEntity?) = ProjectPartnerReportContributionEntity(
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

        private fun expenditure(id: Long, attachment: JemsFileMetadataEntity?) = PartnerReportExpenditureCostEntity(
            id = id,
            number = 1,
            partnerReport = mockk(),
            reportLumpSum = null,
            reportUnitCost = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            reportInvestment = null,
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
            partOfSample = false,
            certifiedAmount = ONE,
            deductedAmount = ZERO,
            typologyOfErrorId = null,
            verificationComment = null,
            parked = false,
            unParkedFrom = null,
            reportOfOrigin = null,
            originalNumber = null,
            reportProjectOfOrigin = null,
            partOfSampleLocked = false
        )

        private fun fileCreate(name: String = "new_file.txt", type: JemsFileType) = JemsFileCreate(
            projectId = 6666L,
            partnerId = PARTNER_ID,
            name = name,
            path = "our/indexed/path/",
            type = type,
            size = 45L,
            content = mockk(),
            userId = USER_ID,
        )

        private val dummyResult = JemsFile(
            id = 478L,
            name = "attachment.pdf",
            type = JemsFileType.Contribution,
            uploaded = LAST_WEEK,
            author = mockk(),
            size = 47889L,
            description = "desc",
            indexedPath = "indexed/path"
        )

       private val dummyResultSimple = JemsFileMetadata(
            id = 478L,
            name = "attachment.pdf",
            uploaded = LAST_WEEK,
        )

    }

    @MockK
    lateinit var reportFileRepository: JemsFileMetadataRepository

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
    lateinit var reportProcurementAttachmentRepository: ProjectPartnerReportProcurementAttachmentRepository

    @MockK
    lateinit var procurementRepository: ProjectPartnerReportProcurementRepository

    @MockK
    lateinit var fileRepository: JemsProjectFileService

    @MockK
    lateinit var reportProcurementGdprAttachmentRepository: ProjectPartnerReportProcurementGdprAttachmentRepository

    @InjectMockKs
    lateinit var persistence: ProjectPartnerReportFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(minioStorage)
        clearMocks(reportFileRepository)
        clearMocks(fileRepository)
    }

    @Test
    fun updatePartnerReportActivityAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>()

        val activity = activity(id = 80L, attachment = oldFile)
        every { workPlanActivityRepository.findById(80L) } returns Optional.of(activity)

        val fileCreate = fileCreate(type = JemsFileType.Activity)
        every { fileRepository.persistFileAndPerformAction(fileCreate, any()) } returns dummyResult
        mockFileDeletion(oldFile)

        assertThat(persistence.updatePartnerReportActivityAttachment(80L, file = fileCreate))
            .isEqualTo(dummyResultSimple)
    }

    @Test
    fun updatePartnerReportDeliverableAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>()

        val deliverable = deliverable(id = 90L, attachment = oldFile)
        every { workPlanActivityDeliverableRepository.findById(90L) } returns Optional.of(deliverable)

        val fileCreate = fileCreate(type = JemsFileType.Deliverable)
        every { fileRepository.persistFileAndPerformAction(fileCreate, any()) } returns dummyResult
        mockFileDeletion(oldFile)

        assertThat(persistence.updatePartnerReportDeliverableAttachment(90L, file = fileCreate))
            .isEqualTo(dummyResultSimple)
    }

    @Test
    fun updatePartnerReportContributionAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>()

        val contribution = contribution(id = 88L, attachment = oldFile)
        every { contributionRepository.findById(50L) } returns Optional.of(contribution)

        val fileCreate = fileCreate(type = JemsFileType.Contribution)
        every { fileRepository.persistFileAndPerformAction(fileCreate, any()) } returns dummyResult
        mockFileDeletion(oldFile)

        assertThat(persistence.updatePartnerReportContributionAttachment(50L, file = fileCreate))
            .isEqualTo(dummyResultSimple)
    }

    @Test
    fun updatePartnerReportOutputAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>()

        val output = output(id = 70L, attachment = oldFile)
        every { workPlanOutputRepository.findById(70L) } returns Optional.of(output)

        val fileCreate = fileCreate(type = JemsFileType.Output)
        every { fileRepository.persistFileAndPerformAction(fileCreate, any()) } returns dummyResult
        mockFileDeletion(oldFile)

        assertThat(persistence.updatePartnerReportOutputAttachment(70L, file = fileCreate))
            .isEqualTo(dummyResultSimple)
    }

    @Test
    fun updatePartnerReportExpenditureAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>()

        val expenditure = expenditure(id = 90L, attachment = oldFile)
        every { expenditureRepository.findById(40L) } returns Optional.of(expenditure)

        val fileCreate = fileCreate(type = JemsFileType.Expenditure)
        every { fileRepository.persistFileAndPerformAction(fileCreate, any()) } returns dummyResult
        mockFileDeletion(oldFile)

        assertThat(persistence.updatePartnerReportExpenditureAttachment(40L, file = fileCreate))
            .isEqualTo(dummyResultSimple)
    }

    @Test
    fun addPartnerReportProcurementAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>() // this is not used here
        mockFileDeletion(oldFile)

        val procurementId = 500L
        val procurement = mockk<ProjectPartnerReportProcurementEntity>()
        every { procurementRepository.getById(procurementId) } returns procurement

        val fileCreate = fileCreate(type = JemsFileType.ProcurementAttachment)
        val extraStep = slot<(JemsFileMetadataEntity) -> Unit>()
        every { fileRepository.persistFileAndPerformAction(fileCreate, capture(extraStep)) } returns dummyResult

        assertThat(persistence
            .addPartnerReportProcurementAttachment(reportId = 48L, file = fileCreate, procurementId = procurementId)
        ).isEqualTo(dummyResultSimple)
    }

    @Test
    fun addPartnerReportProcurementGdprAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>() // this is not used here
        mockFileDeletion(oldFile)

        val procurementId = 500L
        val procurement = mockk<ProjectPartnerReportProcurementEntity>()
        every { procurementRepository.getById(procurementId) } returns procurement

        val fileCreate = fileCreate(type = JemsFileType.ProcurementGdprAttachment)
        val extraStep = slot<(JemsFileMetadataEntity) -> Unit>()
        every { fileRepository.persistFileAndPerformAction(fileCreate, capture(extraStep)) } returns dummyResult

        assertThat(persistence
            .addPartnerReportProcurementGdprAttachment(reportId = 48L, file = fileCreate, procurementId = procurementId)
        ).isEqualTo(dummyResultSimple)
    }

    private fun mockFileDeletion(
        oldFile: JemsFileMetadataEntity,
    ) {
        every { oldFile.minioBucket } returns "bucket"
        every { oldFile.minioLocation } returns "remove/me.pdf"

        every { fileRepository.delete(oldFile) } answers { }
    }

    @Test
    fun addAttachmentToPartnerReport() {
        val fileCreate = fileCreate(type = JemsFileType.PartnerReport).copy(name = "new_file_to_partner.txt")
        every { fileRepository.persistFileAndPerformAction(fileCreate, any()) } returns dummyResult

        assertThat(persistence.addAttachmentToPartnerReport(file = fileCreate)).isEqualTo(dummyResult)
    }
}
