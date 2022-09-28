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
import io.cloudflight.jems.server.project.service.checklist.getInstances.control.GetControlChecklistDetailNotAllowedException
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
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.utils.user
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class DeleteControlChecklistInstanceTest : UnitTest() {

    private val checklistId = 100L
    private val creatorId = 1L
    private val partnerId = 2L
    private val partnerName = "LP1"
    private val reportId = 3L
    private val projectId = 5L

    private val controlCheckLisDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
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
                OptionsToggleInstanceMetadata("yes","test")
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

    private val controlCheckLisDetailWithFinishStatus = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CONTROL,
        name = "name",
        creatorEmail = "a@a",
        creatorId = creatorId,
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

    @InjectMockKs
    lateinit var deleteControlChecklistInstance: DeleteControlChecklistInstance

    @Test
    fun `delete control checklist - OK`() {
        every { userAuthorization.getUser() } returns user
        every { securityService.getUserIdOrThrow() } returns user.id
        every { persistence.getChecklistDetail(checklistId) } returns controlCheckLisDetail
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.deleteById(checklistId) } answers {}
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { partnerPersistence.getById(partnerId) } returns projectPartner
        deleteControlChecklistInstance.deleteById(partnerId, reportId, checklistId)
        verify { persistence.deleteById(checklistId) }

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CHECKLIST_DELETED,
                project = AuditProject(id = projectId.toString()),
                description = "Checklist '${controlCheckLisDetail.id}' type '${controlCheckLisDetail.type}' name '${controlCheckLisDetail.name}' " +
                        "for partner '${partnerName}' and partner report 'R.${reportId}' was deleted"
            )
        )
    }

    @Test
    fun `delete control checklist - does not exist`() {
        every { persistence.getChecklistDetail(-1) } throws GetControlChecklistDetailNotAllowedException()
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { partnerPersistence.getById(partnerId) } returns projectPartner
        assertThrows<GetControlChecklistDetailNotAllowedException> {
            deleteControlChecklistInstance.deleteById(partnerId, reportId, -1L)
        }
    }

    @Test
    fun `delete control checklist - is already in FINISHED status (cannot be deleted)`() {
        every { persistence.getChecklistDetail(checklistId) } returns controlCheckLisDetailWithFinishStatus
        every { partnerPersistence.getProjectIdForPartnerId(partnerId) } returns projectId
        every { partnerPersistence.getById(partnerId) } returns projectPartner
        assertThrows<DeleteControlChecklistInstanceStatusNotAllowedException> { deleteControlChecklistInstance.deleteById(partnerId, reportId, checklistId) }
    }
}