package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionExtendedDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionLineDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.controller.auditAndControl.correction.ProjectAuditCorrectionController
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.closeProjectAuditCorrection.CloseProjectAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.createProjectAuditCorrection.CreateProjectAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.deleteProjectAuditCorrection.DeleteProjectAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.getProjectAuditCorrection.GetProjectAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getPartnerAndPartnerReportData.GetPartnerAndPartnerReportDataInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.listProjectAuditCorrection.ListProjectAuditControlCorrectionsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class ProjectAuditCorrectionControllerTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "test",
            callId = 1L,
            callName = "call",
            acronym = "test",
            status = ApplicationStatus.CONTRACTED,
        )

        private val correction = ProjectAuditControlCorrection(
            id = 1L,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 10,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )

        private val expectedCorrection = ProjectAuditControlCorrectionDTO(
            id = 1L,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 10,
            status = CorrectionStatusDTO.Ongoing,
            linkedToInvoice = true,
        )

        private val extendedCorrection = ProjectAuditControlCorrectionExtended(
            correction = correction,
            auditControlNumber = 20,
            projectCustomIdentifier = projectSummary.customIdentifier
        )
        private val expectedExtendedCorrection = ProjectAuditControlCorrectionExtendedDTO(
            correction = expectedCorrection,
            auditControlNumber = 20,
        )

        private val correctionLines = listOf(
            ProjectAuditControlCorrectionLine(
                id = CORRECTION_ID,
                auditControlId = AUDIT_CONTROL_ID,
                orderNr = 1,
                status = CorrectionStatus.Ongoing,
                linkedToInvoice = true,
                auditControlNumber = 1,
                canBeDeleted = true
            )
        )
        private val expectedCorrectionLines = listOf(
            ProjectAuditControlCorrectionLineDTO(
                id = CORRECTION_ID,
                auditControlId = AUDIT_CONTROL_ID,
                orderNr = 1,
                status = CorrectionStatusDTO.Ongoing,
                linkedToInvoice = true,
                auditControlNumber = 1,
                canBeDeleted = true,


                partnerRoleDTO = ProjectPartnerRoleDTO.PARTNER,
                partnerNumber = 1,
                partnerDisabled = false,
                partnerReport = "",
                initialAuditNUmber = 1,
                initialCorrectionNumber = 1,
                fundName = "",
                fundAmount = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                autoPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                total = BigDecimal.ZERO,
                impactProjectLevel = "",
                scenario = 1
            )
        )

    }

    @MockK
    lateinit var createProjectCorrection: CreateProjectAuditControlCorrectionInteractor

    @MockK
    lateinit var getProjectAuditCorrection: GetProjectAuditControlCorrectionInteractor

    @MockK
    lateinit var listProjectAuditCorrections: ListProjectAuditControlCorrectionsInteractor

    @MockK
    lateinit var deleteProjectAuditCorrection: DeleteProjectAuditControlCorrectionInteractor

    @MockK
    lateinit var closeProjectCorrection: CloseProjectAuditControlCorrectionInteractor

    @MockK
    lateinit var partnerDataInteractor: GetPartnerAndPartnerReportDataInteractor

    @InjectMockKs
    lateinit var projectAuditCorrectionController: ProjectAuditCorrectionController

    @Test
    fun createProjectAuditCorrection() {
        every {
            createProjectCorrection.createProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                true
            )
        } returns correction

        assertThat(
            projectAuditCorrectionController.createProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                true
            )
        ).isEqualTo(expectedCorrection)
    }

    @Test
    fun listProjectAuditCorrections() {
        every {
            listProjectAuditCorrections.listProjectAuditCorrections(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                Pageable.unpaged()
            )
        } returns PageImpl(correctionLines)

        assertThat(
            projectAuditCorrectionController.listProjectAuditCorrections(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                Pageable.unpaged()
            ).content
        ).isEqualTo(expectedCorrectionLines)
    }

    @Test
    fun getProjectAuditCorrection() {
        every {
            getProjectAuditCorrection.getProjectAuditCorrection(
                CORRECTION_ID
            )
        } returns extendedCorrection

        assertThat(
            projectAuditCorrectionController.getProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID
            )
        ).isEqualTo(expectedExtendedCorrection)
    }

    @Test
    fun closeProjectCorrection() {
        every {
            closeProjectCorrection.closeProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID
            )
        } returns CorrectionStatus.Closed

        assertThat(
            projectAuditCorrectionController.closeProjectCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID
            )
        ).isEqualTo(CorrectionStatusDTO.Closed)
    }

}
