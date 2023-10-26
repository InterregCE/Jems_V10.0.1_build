package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionFollowUpTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.controller.auditAndControl.correction.ProjectAuditCorrectionIdentificationController
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification.GetProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getProjectCorrectionIdentification.GetProjectPreviousClosedCorrectionsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.updateCorrectionIdentification.UpdateProjectCorrectionIdentificationInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class ProjectAuditCorrectionIdentificationControllerTest : UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L
        private const val CORRECTION_ID_2 = 2L
        private const val PROGRAMME_FUND_ID = 11L
        private val zonedDateNow = ZonedDateTime.now()
        private val zonedDateTomorrow = ZonedDateTime.now().plusDays(1)

        private val correction = ProjectAuditControlCorrection(
            id = CORRECTION_ID_2,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 10,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )
        private val correctionIdentification = ProjectCorrectionIdentification(
            correction = correction,
            followUpOfCorrectionId = CORRECTION_ID,
            correctionFollowUpType = CorrectionFollowUpType.No,
            repaymentFrom = zonedDateNow,
            lateRepaymentTo = zonedDateTomorrow,
            partnerId = 1L,
            partnerReportId = 3L,
            programmeFundId = PROGRAMME_FUND_ID,
        )
        private val expectedCorrectionIdentification = ProjectCorrectionIdentificationDTO(
            correctionId = CORRECTION_ID_2,
            followUpOfCorrectionId = CORRECTION_ID,
            correctionFollowUpType = CorrectionFollowUpTypeDTO.No,
            repaymentFrom = zonedDateNow,
            lateRepaymentTo = zonedDateTomorrow,
            partnerId = 1L,
            partnerReportId = 3L,
            programmeFundId = PROGRAMME_FUND_ID,
        )

        private val expectedCorrection = ProjectAuditControlCorrectionDTO(
            id = CORRECTION_ID_2,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 10,
            status = CorrectionStatusDTO.Ongoing,
            linkedToInvoice = true,
        )

        private val correctionIdentificationUpdate = ProjectCorrectionIdentificationUpdateDTO(
            followUpOfCorrectionId = CORRECTION_ID,
            correctionFollowUpType = CorrectionFollowUpTypeDTO.No,
            repaymentFrom = zonedDateNow,
            lateRepaymentTo = zonedDateTomorrow,
            partnerId = 1L,
            partnerReportId = 3L,
            programmeFundId = PROGRAMME_FUND_ID
        )

    }


    @MockK
    lateinit var getProjectCorrectionIdentification: GetProjectCorrectionIdentification

    @MockK
    lateinit var getPreviousClosedCorrections: GetProjectPreviousClosedCorrectionsInteractor

    @MockK
    lateinit var updateProjectCorrectionInteractor: UpdateProjectCorrectionIdentificationInteractor

    @InjectMockKs
    lateinit var controller: ProjectAuditCorrectionIdentificationController

    @Test
    fun getCorrectionIdentification() {
        every {
            getProjectCorrectionIdentification.getProjectCorrectionIdentification(
                CORRECTION_ID
            )
        } returns correctionIdentification

        assertThat(
            controller.getCorrectionIdentification(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID
            )
        ).isEqualTo(expectedCorrectionIdentification)
    }

    @Test
    fun getPreviousClosedCorrections() {
        every {
            getPreviousClosedCorrections.getProjectPreviousClosedCorrections(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID
            )
        } returns listOf(correction)

        assertThat(
            controller.getPreviousClosedCorrections(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID
            )
        ).isEqualTo(listOf(expectedCorrection))
    }

    @Test
    fun updateCorrectionIdentification() {
        every {
            updateProjectCorrectionInteractor.updateProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID,
                correctionIdentificationUpdate.toModel()
            )
        } returns correctionIdentification

        assertThat(
            controller.updateCorrectionIdentification(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID,
                correctionIdentificationUpdate
            )
        ).isEqualTo(expectedCorrectionIdentification)
    }

}
