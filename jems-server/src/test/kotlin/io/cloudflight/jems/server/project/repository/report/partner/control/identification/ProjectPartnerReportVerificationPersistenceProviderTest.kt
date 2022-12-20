package io.cloudflight.jems.server.project.repository.report.partner.control.identification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportOnTheSpotVerificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportVerificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportVerificationGeneralMethodologyEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportVerificationOnTheSpotLocationEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportLocationOnTheSpotVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportMethodology
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportOnTheSpotVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Optional

class ProjectPartnerReportVerificationPersistenceProviderTest: UnitTest() {

    companion object {

        private fun entity(
            reportId: Long,
            methodologyId: Long,
            OnTheSpotVerificationId: Long,
            OnTheSpotVerificationLocationId: Long,
            report: ProjectPartnerReportEntity
        ) = ProjectPartnerReportVerificationEntity(
            reportId = reportId,
            reportEntity = report,
            generalMethodologies = mutableSetOf(
                ProjectPartnerReportVerificationGeneralMethodologyEntity(
                    id = methodologyId,
                    reportVerificationId = 1L,
                    methodology = ReportMethodology.AdministrativeVerification
                )
            ),
            verificationInstances = mutableListOf(
                ProjectPartnerReportOnTheSpotVerificationEntity(
                    id = OnTheSpotVerificationId,
                    reportVerificationId = 1L,
                    verificationFrom = LocalDate.now(),
                    verificationTo = LocalDate.now().plusDays(1),
                    verificationLocations = if (OnTheSpotVerificationId == 0L) mutableSetOf() else mutableSetOf(
                        ProjectPartnerReportVerificationOnTheSpotLocationEntity(
                            id = OnTheSpotVerificationLocationId,
                            reportOnTheSpotVerificationId = 1L,
                            location = ReportLocationOnTheSpotVerification.PremisesOfProjectPartner
                        )
                    ),
                    verificationFocus = "some focus"
                )
            ),
            riskBasedVerificationApplied = true,
            riskBasedVerificationDescription = "some description"
        )

        val existingOnTheSpot = ProjectPartnerReportOnTheSpotVerificationEntity(
            id = 1,
            reportVerificationId = 1L,
            verificationFrom = LocalDate.now(),
            verificationTo = LocalDate.now().plusDays(1),
            verificationLocations = mutableSetOf(
                ProjectPartnerReportVerificationOnTheSpotLocationEntity(
                    id = 1,
                    reportOnTheSpotVerificationId = 1L,
                    location = ReportLocationOnTheSpotVerification.ProjectEvent
                )
            ),
            verificationFocus = "some wrong focus"
        )

        private fun updateEntity(
            reportId: Long,
            report: ProjectPartnerReportEntity
        ) = ProjectPartnerReportVerificationEntity(
            reportId = reportId,
            reportEntity = report,
            generalMethodologies = mutableSetOf(),
            verificationInstances = mutableListOf(),
            riskBasedVerificationApplied = false,
            riskBasedVerificationDescription = "some wrong description"
        )

        val onTheSpotVerificationEntityToBeSaved = ProjectPartnerReportOnTheSpotVerificationEntity(
            id = 0L,
            reportVerificationId = 1L,
            verificationFrom = LocalDate.now(),
            verificationTo = LocalDate.now().plusDays(1),
            verificationLocations = mutableSetOf(),
            verificationFocus = "some focus"
        )

        val onTheSpotVerificationEntitySavedWithoutLocations = ProjectPartnerReportOnTheSpotVerificationEntity(
            id = 1L,
            reportVerificationId = 1L,
            verificationFrom = LocalDate.now(),
            verificationTo = LocalDate.now().plusDays(1),
            verificationLocations = mutableSetOf(),
            verificationFocus = "some focus"
        )

        val onTheSpotVerificationEntityToBeSavedWithoutLocations = ProjectPartnerReportOnTheSpotVerificationEntity(
            id = 1L,
            reportVerificationId = 1L,
            verificationFrom = LocalDate.now(),
            verificationTo = LocalDate.now().plusDays(1),
            verificationLocations = mutableSetOf(
                ProjectPartnerReportVerificationOnTheSpotLocationEntity(
                    id = 0L,
                    reportOnTheSpotVerificationId = 1L,
                    location = ReportLocationOnTheSpotVerification.PremisesOfProjectPartner
                )
            ),
            verificationFocus = "some focus"
        )

        val onTheSpotVerificationEntity = ProjectPartnerReportOnTheSpotVerificationEntity(
            id = 1L,
            reportVerificationId = 1L,
            verificationFrom = LocalDate.now(),
            verificationTo = LocalDate.now().plusDays(1),
            verificationLocations = mutableSetOf(
                ProjectPartnerReportVerificationOnTheSpotLocationEntity(
                    id = 1L,
                    reportOnTheSpotVerificationId = 1L,
                    location = ReportLocationOnTheSpotVerification.PremisesOfProjectPartner
                )
            ),
            verificationFocus = "some focus"
        )

        val dto = ReportVerification(
            generalMethodologies = mutableSetOf(ReportMethodology.AdministrativeVerification),
            verificationInstances = mutableListOf(
                ReportOnTheSpotVerification(
                    id = 0L,
                    verificationFrom = LocalDate.now(),
                    verificationTo = LocalDate.now().plusDays(1),
                    verificationLocations = mutableSetOf(
                        ReportLocationOnTheSpotVerification.PremisesOfProjectPartner
                    ),
                    verificationFocus = "some focus"
                )
            ),
            riskBasedVerificationApplied = true,
            riskBasedVerificationDescription = "some description"
        )

        val updateDto = ReportVerification(
            generalMethodologies = mutableSetOf(ReportMethodology.AdministrativeVerification),
            verificationInstances = mutableListOf(
                ReportOnTheSpotVerification(
                    id = 1L,
                    verificationFrom = LocalDate.now(),
                    verificationTo = LocalDate.now().plusDays(1),
                    verificationLocations = mutableSetOf(
                        ReportLocationOnTheSpotVerification.PremisesOfProjectPartner
                    ),
                    verificationFocus = "some focus"
                )
            ),
            riskBasedVerificationApplied = true,
            riskBasedVerificationDescription = "some description"
        )

        val expectedDto = ReportVerification(
            generalMethodologies = mutableSetOf(ReportMethodology.AdministrativeVerification),
            verificationInstances = mutableListOf(
                ReportOnTheSpotVerification(
                    id = 1L,
                    verificationFrom = LocalDate.now(),
                    verificationTo = LocalDate.now().plusDays(1),
                    verificationLocations = mutableSetOf(
                        ReportLocationOnTheSpotVerification.PremisesOfProjectPartner
                    ),
                    verificationFocus = "some focus"
                )
            ),
            riskBasedVerificationApplied = true,
            riskBasedVerificationDescription = "some description"
        )
    }

    @MockK
    private lateinit var verificationRepository: ProjectPartnerReportVerificationRepository

    @MockK
    private lateinit var onTheSpotVerificationRepository: ProjectPartnerReportOnTheSpotVerificationRepository

    @MockK
    private lateinit var reportRepository: ProjectPartnerReportRepository

    @InjectMockKs
    private lateinit var persistence: ProjectPartnerReportVerificationPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(verificationRepository)
        clearMocks(onTheSpotVerificationRepository)
        clearMocks(reportRepository)
    }

    @Test
    fun getControlReportVerification() {
        val report = mockk<ProjectPartnerReportEntity>()
        every {
            report.id
        } returns 1L



        every {
            verificationRepository.findByReportEntityIdAndReportEntityPartnerId(1L, 1L)
        } returns Optional.of(entity(1, 1, 1, 1, report))
        Assertions.assertThat(
            persistence.getControlReportVerification(
                1L,
                reportId = 1L
            ).get()
        ).isEqualTo(expectedDto)
    }

    @Test
    fun `update Report Verification - create new`() {
        val report = mockk<ProjectPartnerReportEntity>()
        every {
            report.id
        } returns 1L

        every { reportRepository.findByIdAndPartnerId(1L, 1L) } returns report

        every {
            verificationRepository.findByReportEntityIdAndReportEntityPartnerId(1L, 1L)
        } returns Optional.empty()

        every {
            verificationRepository.save(entity(0, 0, 0, 0, report))
        } returns entity(1, 0, 0, 0, report)

        every {
            verificationRepository.save(entity(1, 0, 1, 1, report))
        } returns entity(1, 1, 1, 1,report)

        every { onTheSpotVerificationRepository.findById(0L) } returns Optional.empty()

        every {
            onTheSpotVerificationRepository.save(onTheSpotVerificationEntityToBeSaved)
        } returns onTheSpotVerificationEntitySavedWithoutLocations

        every {
            onTheSpotVerificationRepository.save(onTheSpotVerificationEntityToBeSavedWithoutLocations)
        } returns onTheSpotVerificationEntity

        Assertions.assertThat(
            persistence.updateReportVerification(
                1L,
                reportId = 1L,
                dto
            )
        ).isEqualTo(expectedDto)
    }

    @Test
    fun `update Report Verification - update existing but no table data`() {
        val report = mockk<ProjectPartnerReportEntity>()
        every {
            report.id
        } returns 1L

        every { reportRepository.findByIdAndPartnerId(1L, 1L) } returns report

        every {
            verificationRepository.findByReportEntityIdAndReportEntityPartnerId(1L, 1L)
        } returns Optional.of(updateEntity(1, report))

        every {
            verificationRepository.save(entity(1, 0, 1, 1, report))
        } returns entity(1, 1, 1, 1,report)

        every { onTheSpotVerificationRepository.findById(0L) } returns Optional.empty()

        every {
            onTheSpotVerificationRepository.save(onTheSpotVerificationEntityToBeSaved)
        } returns onTheSpotVerificationEntitySavedWithoutLocations

        every {
            onTheSpotVerificationRepository.save(onTheSpotVerificationEntityToBeSavedWithoutLocations)
        } returns onTheSpotVerificationEntity

        Assertions.assertThat(
            persistence.updateReportVerification(
                1L,
                reportId = 1L,
                dto
            )
        ).isEqualTo(expectedDto)
    }

    @Test
    fun `update Report Verification - update existing`() {
        val report = mockk<ProjectPartnerReportEntity>()
        every {
            report.id
        } returns 1L

        every { reportRepository.findByIdAndPartnerId(1L, 1L) } returns report

        every {
            verificationRepository.findByReportEntityIdAndReportEntityPartnerId(1L, 1L)
        } returns Optional.of(entity(1, 1, 1, 1, report))

        every {
            verificationRepository.save(entity(1, 0, 1, 1, report))
        } returns entity(1, 1, 1, 1,report)

        every { onTheSpotVerificationRepository.findById(1L) } returns Optional.of(existingOnTheSpot)

        every {
            onTheSpotVerificationRepository.save(onTheSpotVerificationEntityToBeSavedWithoutLocations)
        } returns onTheSpotVerificationEntity

        Assertions.assertThat(
            persistence.updateReportVerification(
                1L,
                reportId = 1L,
                updateDto
            )
        ).isEqualTo(expectedDto)
    }
}
