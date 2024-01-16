package io.cloudflight.jems.server.project.repository.report.partner.control.identification

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionRepository
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.identification.ProjectPartnerReportDesignatedControllerEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportDesignatedController
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

class ProjectPartnerReportDesignatedControllerPersistenceProviderTest: UnitTest() {
    companion object {
        private fun designatedControllerEntity(
            report: ProjectPartnerReportEntity,
            controlInstitution: ControllerInstitutionEntity,
            controlUser: UserEntity,
            reviewUser: UserEntity,
        ) = ProjectPartnerReportDesignatedControllerEntity(
            reportId = 1L,
            reportEntity = report,
            controlInstitution = controlInstitution,
            controllingUser = controlUser,
            controllerReviewer = reviewUser,
            jobTitle = "some title",
            divisionUnit = "some division",
            address = "some address",
            countryCode = "RO",
            country = "Romania (RO)",
            telephone = "0744123456",
            institutionName = "some name"
        )

        private fun updatedDesignatedController(
            controlInstitution: ControllerInstitutionEntity,
            controlUser: UserEntity,
            reviewUser: UserEntity,
        ) = ReportDesignatedController(
            controlInstitution = controlInstitution.name,
            controlInstitutionId = controlInstitution.id,
            jobTitle = "some title changed",
            divisionUnit = "some division changed",
            address = "some address changed",
            countryCode = "RO",
            country = "Romania (RO)",
            telephone = "1234567890",
            controllerReviewerId = reviewUser.id,
            controllingUserId = controlUser.id
        )

        val expectedDesignatedController = ReportDesignatedController(
            controlInstitution = "some name",
            controlInstitutionId = 1L,
            controllingUserId = 1L,
            jobTitle = "some title",
            divisionUnit = "some division",
            address = "some address",
            countryCode = "RO",
            country = "Romania (RO)",
            telephone = "0744123456",
            controllerReviewerId = 2L
        )

        val expectedUpdatedDesignatedController = ReportDesignatedController(
            controlInstitution = "some name",
            controlInstitutionId = 1L,
            controllingUserId = 1L,
            jobTitle = "some title changed",
            divisionUnit = "some division changed",
            address = "some address changed",
            countryCode = "RO",
            country = "Romania (RO)",
            telephone = "1234567890",
            controllerReviewerId = 2L
        )
    }
    @MockK
    private lateinit var designatedControllerRepository: ProjectPartnerReportDesignatedControllerRepository

    @MockK
    private lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    private lateinit var institutionRepository: ControllerInstitutionRepository

    @MockK
    private lateinit var userRepo: UserRepository

    @InjectMockKs
    private lateinit var persistence: ProjectPartnerReportDesignatedControllerPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(designatedControllerRepository)
        clearMocks(reportRepository)
        clearMocks(institutionRepository)
        clearMocks(userRepo)
    }

    @Test
    fun getControlReportDesignatedController() {
        val report = mockk<ProjectPartnerReportEntity>()
        val controlInstitution = mockk<ControllerInstitutionEntity>()
        val controlUser = mockk<UserEntity>()
        val reviewUser = mockk<UserEntity>()
        every {
            report.id
        } returns 1L
        every {
            report.status
        } returns ReportStatus.InControl
        every {
            controlInstitution.id
        } returns 1L
        every {
            controlInstitution.name
        } returns "some name"
        every {
            controlUser.id
        } returns 1L
        every {
            reviewUser.id
        } returns 2L
        every {
            designatedControllerRepository.findByReportEntityIdAndReportEntityPartnerId(1L, 1L)
        } returns designatedControllerEntity(report, controlInstitution, controlUser, reviewUser)
        assertThat(
            persistence.getControlReportDesignatedController(
                1L,
                reportId = 1L
            )
        ).isEqualTo(expectedDesignatedController)
    }

    @Test
    fun createWithInstitution() {
        val report = mockk<ProjectPartnerReportEntity>()
        val controlInstitutionEntity = mockk<ControllerInstitutionEntity>()
        val controlInstitution = ControllerInstitutionList(
            id = 1L,
            name = "some institution name",
            description = null,
            institutionNuts = emptyList(),
            createdAt = null
        )

        val entitySaved = ProjectPartnerReportDesignatedControllerEntity(
            reportId = 1L,
            reportEntity = report,
            controlInstitution = controlInstitutionEntity,
            controllingUser = null,
            controllerReviewer = null,
            jobTitle = null,
            divisionUnit = null,
            address = null,
            countryCode = null,
            country = null,
            telephone = null,
            institutionName = "some institution name"
        )

        every {
            report.id
        } returns 1L
        every {
            controlInstitutionEntity.name
        } returns "some name"
        every {
            reportRepository.findByIdAndPartnerId(1L, 1L)
        } returns report
        every {
            institutionRepository.findById(1L)
        } returns Optional.of(controlInstitutionEntity)
        every {
            designatedControllerRepository.save(any())
        } returns entitySaved

        persistence.create(
            1L,
            1L,
            controlInstitution.id
        )

        verify(exactly = 1) { designatedControllerRepository.save(any()) }
    }

    @Test
    fun updateDesignatedController() {
        val report = mockk<ProjectPartnerReportEntity>()
        val controlInstitution = mockk<ControllerInstitutionEntity>()
        val controlUser = mockk<UserEntity>()
        val reviewUser = mockk<UserEntity>()
        every {
            report.id
        } returns 1L
        every {
            report.status
        } returns ReportStatus.InControl
        every {
            controlInstitution.id
        } returns 1L
        every {
            controlInstitution.name
        } returns "some name"
        every {
            controlUser.id
        } returns 1L
        every {
            reviewUser.id
        } returns 2L

        val updateDesignatedController =
            updatedDesignatedController(controlInstitution, controlUser, reviewUser)

        every {
            designatedControllerRepository.findByReportEntityIdAndReportEntityPartnerId(1L, 1L)
        } returns designatedControllerEntity(report, controlInstitution, controlUser, reviewUser)
        every {
            userRepo.getReferenceById(1L)
        } returns controlUser

        every {
            userRepo.getReferenceById(2L)
        } returns reviewUser

        assertThat(
            persistence.updateDesignatedController(
                1L,
                1L,
                updateDesignatedController
            )
        ).isEqualTo(expectedUpdatedDesignatedController)

    }
}
