package io.cloudflight.jems.server.project.controller.auditAndControl.correction

import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionFollowUpTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionLineDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.closeAuditControlCorrection.CloseAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.createAuditControlCorrection.CreateAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.deleteAuditControlCorrection.DeleteAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.getAuditControlCorrection.GetAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection.ListAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection.UpdateAuditControlCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.listPreviouslyClosedCorrection.ListPreviouslyClosedCorrectionInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

class AuditControlCorrectionControllerTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val CORRECTION_ID = 1L

        private val correction = AuditControlCorrectionDetail(
            id = 1L,
            orderNr = 10,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToInvoice,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNr = 36,
            followUpOfCorrectionId = 75L,
            correctionFollowUpType = CorrectionFollowUpType.CourtProcedure,
            repaymentFrom = LocalDate.of(2023, 11, 9),
            lateRepaymentTo = LocalDate.of(2023, 11, 15),
            partnerId = 96L,
            partnerReportId = 960L,
            programmeFundId = 9605L,
        )

        private val expectedCorrection = ProjectAuditControlCorrectionDTO(
            id = 1L,
            orderNr = 10,
            status = AuditStatusDTO.Ongoing,
            type = AuditControlCorrectionTypeDTO.LinkedToInvoice,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNumber = 36,
            followUpOfCorrectionId = 75L,
            correctionFollowUpType = CorrectionFollowUpTypeDTO.CourtProcedure,
            repaymentFrom = LocalDate.of(2023, 11, 9),
            lateRepaymentTo = LocalDate.of(2023, 11, 15),
            partnerId = 96L,
            partnerReportId = 960L,
            programmeFundId = 9605L,
        )

        private val extendedCorrection = AuditControlCorrectionDetail(
            id = 1L,
            orderNr = 10,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToCostOption,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNr = 15,
            followUpOfCorrectionId = 75L,
            correctionFollowUpType = CorrectionFollowUpType.CourtProcedure,
            repaymentFrom = LocalDate.of(2023, 11, 9),
            lateRepaymentTo = LocalDate.of(2023, 11, 15),
            partnerId = 96L,
            partnerReportId = 960L,
            programmeFundId = 9605L,
        )
        private val expectedExtendedCorrection = ProjectAuditControlCorrectionDTO(
            id = 1L,
            orderNr = 10,
            status = AuditStatusDTO.Ongoing,
            type = AuditControlCorrectionTypeDTO.LinkedToCostOption,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNumber = 15,
            followUpOfCorrectionId = 75L,
            correctionFollowUpType = CorrectionFollowUpTypeDTO.CourtProcedure,
            repaymentFrom = LocalDate.of(2023, 11, 9),
            lateRepaymentTo = LocalDate.of(2023, 11, 15),
            partnerId = 96L,
            partnerReportId = 960L,
            programmeFundId = 9605L,
        )

        private val correctionLines = listOf(
            AuditControlCorrectionLine(
                id = CORRECTION_ID,
                orderNr = 1,
                status = AuditControlStatus.Ongoing,
                type = AuditControlCorrectionType.LinkedToCostOption,
                auditControlId = AUDIT_CONTROL_ID,
                auditControlNr = 1,
                canBeDeleted = true,
            )
        )
        private val expectedCorrectionLines = listOf(
            ProjectAuditControlCorrectionLineDTO(
                id = CORRECTION_ID,
                auditControlId = AUDIT_CONTROL_ID,
                orderNr = 1,
                status = AuditStatusDTO.Ongoing,
                type = AuditControlCorrectionTypeDTO.LinkedToCostOption,
                auditControlNumber = 1,
                canBeDeleted = true,


                partnerRoleDTO = ProjectPartnerRoleDTO.PARTNER,
                partnerNumber = 0,
                partnerDisabled = false,
                partnerReport = "",
                initialAuditNUmber = 0,
                initialCorrectionNumber = 0,
                fundName = "",
                fundAmount = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                autoPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                total = BigDecimal.ZERO,
                impactProjectLevel = "",
                scenario = 0
            )
        )

    }

    @MockK
    lateinit var createProjectCorrection: CreateAuditControlCorrectionInteractor

    @MockK
    lateinit var listProjectAuditCorrections: ListAuditControlCorrectionInteractor

    @MockK
    lateinit var getProjectAuditCorrection: GetAuditControlCorrectionInteractor

    @MockK
    lateinit var deleteProjectAuditCorrection: DeleteAuditControlCorrectionInteractor

    @MockK
    lateinit var closeProjectCorrection: CloseAuditControlCorrectionInteractor

    @MockK
    private lateinit var updateCorrection: UpdateAuditControlCorrectionInteractor

    @MockK
    private lateinit var listPreviouslyClosedCorrection: ListPreviouslyClosedCorrectionInteractor

    @InjectMockKs
    lateinit var projectAuditCorrectionController: AuditControlCorrectionController

    @Test
    fun createProjectAuditCorrection() {
        every {
            createProjectCorrection.createCorrection(AUDIT_CONTROL_ID, AuditControlCorrectionType.LinkedToCostOption)
        } returns correction

        assertThat(
            projectAuditCorrectionController.createProjectAuditCorrection(0L, AUDIT_CONTROL_ID, AuditControlCorrectionTypeDTO.LinkedToCostOption)
        ).isEqualTo(expectedCorrection)
    }

    @Test
    fun listProjectAuditCorrections() {
        every {
            listProjectAuditCorrections.listCorrections(AUDIT_CONTROL_ID, Pageable.unpaged())
        } returns PageImpl(correctionLines)

        assertThat(
            projectAuditCorrectionController.listProjectAuditCorrections(0L, AUDIT_CONTROL_ID, Pageable.unpaged())
        ).containsExactlyElementsOf(expectedCorrectionLines)
    }

    @Test
    fun getProjectAuditCorrection() {
        every {
            getProjectAuditCorrection.getCorrection(CORRECTION_ID)
        } returns extendedCorrection

        assertThat(
            projectAuditCorrectionController.getProjectAuditCorrection(0L, AUDIT_CONTROL_ID, CORRECTION_ID)
        ).isEqualTo(expectedExtendedCorrection)
    }

    @Test
    fun closeProjectCorrection() {
        every {
            closeProjectCorrection.closeCorrection(45L)
        } returns AuditControlStatus.Closed

        assertThat(
            projectAuditCorrectionController.closeProjectCorrection(0L, 0L, 45L)
        ).isEqualTo(AuditStatusDTO.Closed)

        verify(exactly = 1) { closeProjectCorrection.closeCorrection(45L) }
    }

}
