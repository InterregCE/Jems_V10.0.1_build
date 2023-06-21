package io.cloudflight.jems.server.project.service.checklist.delete.control

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.utils.user
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher

internal class DeleteControlChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val creatorId = 1L
    private val partnerId = 2L
    private val partnerName = "LP1"
    private val reportId = 3L
    private val projectId = 5L
    private val controlReportId = 2
    private val TODAY = ZonedDateTime.now()

    private val controlChecklistDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
        createdAt = TODAY,
        relatedToId = reportId,
        finishedDate = null,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        consolidated = false,
        visible = false,
        components = mutableListOf(
            ChecklistComponentInstance(
                2L,
                ProgrammeChecklistComponentType.HEADLINE,
                1,
                HeadlineMetadata("headline"),
                HeadlineInstanceMetadata()
            ),
            ChecklistComponentInstance(
                3L,
                ProgrammeChecklistComponentType.OPTIONS_TOGGLE,
                2,
                OptionsToggleMetadata("What option do you choose", "yes", "no", "maybe", ""),
                OptionsToggleInstanceMetadata("yes", "test")
            ),
            ChecklistComponentInstance(
                4L,
                ProgrammeChecklistComponentType.TEXT_INPUT,
                3,
                TextInputMetadata("Question to be answered", "Label", 2000),
                TextInputInstanceMetadata("Explanation")
            )
        )
    )

    private val controlChecklistDetailWithFinishStatus = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
        createdAt = TODAY,
        relatedToId = reportId,
        finishedDate = null,
        consolidated = false,
        visible = true,
        minScore = BigDecimal(0),
        maxScore = BigDecimal(10),
        allowsDecimalScore = false,
        components = emptyList()
    )

    private val projectPartner = ProjectPartnerDetail(
        projectId = 5,
        id = 2L,
        active = true,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        createdAt = ZonedDateTime.now(),
        sortNumber = 1,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
        nace = NaceGroupLevel.A,
        otherIdentifierNumber = null,
        otherIdentifierDescription = emptySet(),
        pic = null,
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes,
        legalStatusId = 3L,
        addresses = listOf(
            ProjectPartnerAddress(
                type = ProjectPartnerAddressType.Organization,
                country = "country",
                nutsRegion2 = "nutsRegion2",
                nutsRegion3 = "nutsRegion3",
                street = "street",
                houseNumber = "houseNumber",
                postalCode = "postalCode",
                city = "city",
                homepage = "homepage"
            )
        ),
        motivation = ProjectPartnerMotivation(
            organizationRelevance = setOf(InputTranslation(SystemLanguage.EN, "organizationRelevance")),
            organizationExperience = setOf(InputTranslation(SystemLanguage.EN, "organizationExperience")),
            organizationRole = setOf(InputTranslation(SystemLanguage.EN, "organizationRole"))
        )
    )

    private fun getProjectPartnerReport(status: ReportStatus) =
        ProjectPartnerReport(
            id = 2,
            reportNumber = 1,
            status = status,
            version = "1.0",
            firstSubmission = null,
            lastResubmission = null,
            controlEnd = TODAY,
            lastControlReopening = null,
            projectReportId = 96L,
            projectReportNumber = 963,
            identification = PartnerReportIdentification(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 2,
                partnerAbbreviation = "",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = null,
                nameInEnglish = null,
                legalStatus = null,
                partnerType = null,
                vatRecovery = null,
                country = null,
                currency = null,
                coFinancing = emptyList(),
            )
        )

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    lateinit var deleteControlChecklistInstance: DeleteControlChecklistInstance

    @Test
    fun `delete control checklist - OK`() {
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)
        every { securityService.currentUser?.user?.id } returns creatorId
        every { securityService.getUserIdOrThrow() } returns user.id
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistDetail
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.deleteById(checklistId) } answers {}
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { partnerPersistence.getById(partnerId) } returns projectPartner
        every { reportPersistence.getPartnerReportById(partnerId, reportId).reportNumber } returns controlReportId
        every { reportPersistence.getPartnerReportById(partnerId, reportId).status } returns ReportStatus.InControl
        every { reportPersistence.getPartnerReportById(partnerId, reportId).controlEnd } returns TODAY
        deleteControlChecklistInstance.deleteById(partnerId, reportId, checklistId)
        verify { persistence.deleteById(checklistId) }

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CHECKLIST_DELETED,
                project = AuditProject(id = projectId.toString()),
                description = "Checklist ${controlChecklistDetail.id} type ${controlChecklistDetail.type} name ${controlChecklistDetail.name} " +
                        "for partner $partnerName and partner report R.$controlReportId was deleted"
            )
        )
    }

    @Test
    fun `delete control checklist - does not exist`() {
        every {
            persistence.getChecklistDetail(
                -1,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } throws GetChecklistInstanceDetailNotFoundException()
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)
        every { securityService.currentUser?.user?.id } returns creatorId
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { partnerPersistence.getById(partnerId) } returns projectPartner
        assertThrows<GetChecklistInstanceDetailNotFoundException> {
            deleteControlChecklistInstance.deleteById(partnerId, reportId, -1L)
        }
    }

    @Test
    fun `delete control checklist - is already in FINISHED status (cannot be deleted)`() {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistDetailWithFinishStatus
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(ReportStatus.InControl)
        every { securityService.currentUser?.user?.id } returns 1
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { partnerPersistence.getById(partnerId) } returns projectPartner
        assertThrows<DeleteControlChecklistInstanceStatusNotAllowedException> {
            deleteControlChecklistInstance.deleteById(
                partnerId,
                reportId,
                checklistId
            )
        }
    }

    @ParameterizedTest
    @EnumSource(value = ReportStatus::class, names = ["InControl"], mode = EnumSource.Mode.EXCLUDE)
    fun `delete control checklist failed - report is certified`(status: ReportStatus) {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CONTROL,
                reportId
            )
        } returns controlChecklistDetailWithFinishStatus
        every{
            reportPersistence.getPartnerReportById(partnerId, reportId)
        } returns getProjectPartnerReport(status)
        every { securityService.currentUser?.user?.id } returns 1
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { partnerPersistence.getById(partnerId) } returns projectPartner
        assertThrows<DeleteControlChecklistInstanceStatusNotAllowedException> {
            deleteControlChecklistInstance.deleteById(
                partnerId,
                reportId,
                checklistId
            )
        }
    }
}
