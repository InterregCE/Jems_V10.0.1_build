package io.cloudflight.jems.server.project.service.checklist.delete.closure

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.*
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.getInstances.GetChecklistInstanceDetailNotFoundException
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.utils.user
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

class DeleteClosureChecklistInstanceTest: UnitTest() {

    private val checklistId = 100L
    private val creatorId = 1L
    private val reportId = 3L
    private val projectId = 5L
    private val controlReportId = 2
    private val TODAY = ZonedDateTime.now()

    private val closureChecklistDetail = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.DRAFT,
        type = ProgrammeChecklistType.CLOSURE,
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

    private val closureChecklistDetailWithFinishStatus = ChecklistInstanceDetail(
        id = checklistId,
        programmeChecklistId = 1L,
        status = ChecklistInstanceStatus.FINISHED,
        type = ProgrammeChecklistType.CLOSURE,
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

    private fun report(status: ProjectReportStatus): ProjectReportModel {
        val report = mockk<ProjectReportModel>()
        every { report.status } returns status
        every { report.projectId } returns projectId
        every { report.reportNumber } returns controlReportId
        return report
    }

    @MockK
    lateinit var persistence: ChecklistInstancePersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var deleteClosureChecklistInstance: DeleteClosureChecklistInstance

    @Test
    fun `delete closure checklist - OK`() {
        every { securityService.currentUser?.user?.id } returns creatorId
        every { securityService.getUserIdOrThrow() } returns user.id
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistDetail
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { persistence.deleteById(checklistId) } answers {}
        every{
            reportPersistence.getReportByIdUnSecured(reportId)
        } returns report(ProjectReportStatus.Draft)

        deleteClosureChecklistInstance.deleteById(reportId, checklistId)
        verify { persistence.deleteById(checklistId) }

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CHECKLIST_DELETED,
                project = AuditProject(id = projectId.toString()),
                description = "Checklist ${closureChecklistDetail.id} type ${closureChecklistDetail.type} name ${closureChecklistDetail.name} " +
                    "for project report R.$controlReportId was deleted"
            )
        )
    }

    @Test
    fun `delete closure checklist - does not exist`() {
        every {
            persistence.getChecklistDetail(
                -1,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } throws GetChecklistInstanceDetailNotFoundException()
        every{
            reportPersistence.getReportByIdUnSecured(reportId)
        } returns report(ProjectReportStatus.Draft)
        every { securityService.currentUser?.user?.id } returns creatorId
        assertThrows<GetChecklistInstanceDetailNotFoundException> {
            deleteClosureChecklistInstance.deleteById(reportId, -1L)
        }
    }

    @Test
    fun `delete closure checklist - is already in FINISHED status (cannot be deleted)`() {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistDetailWithFinishStatus
        every{
            reportPersistence.getReportByIdUnSecured(reportId)
        } returns report(ProjectReportStatus.Draft)
        every { securityService.currentUser?.user?.id } returns 1

        assertThrows<DeleteClosureChecklistInstanceStatusNotAllowedException> {
            deleteClosureChecklistInstance.deleteById(
                reportId,
                checklistId
            )
        }
    }

    @ParameterizedTest
    @EnumSource(value = ProjectReportStatus::class, names = ["Submitted", "InVerification", "Finalized", "ReOpenFinalized"], mode = EnumSource.Mode.INCLUDE)
    fun `delete closure checklist failed - report is closed`(status: ProjectReportStatus) {
        every {
            persistence.getChecklistDetail(
                checklistId,
                ProgrammeChecklistType.CLOSURE,
                reportId
            )
        } returns closureChecklistDetailWithFinishStatus
        every{
            reportPersistence.getReportByIdUnSecured(reportId)
        } returns report(status)
        every { securityService.currentUser?.user?.id } returns 1
        assertThrows<DeleteClosureChecklistInstanceStatusNotAllowedException> {
            deleteClosureChecklistInstance.deleteById(
                reportId,
                checklistId
            )
        }
    }


}
