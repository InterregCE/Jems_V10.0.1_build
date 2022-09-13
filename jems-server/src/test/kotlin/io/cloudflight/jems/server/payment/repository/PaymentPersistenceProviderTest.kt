package io.cloudflight.jems.server.payment.repository

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.createTestCallEntity
import io.cloudflight.jems.server.payments.entity.PaymentToProjectEntity
import io.cloudflight.jems.server.payments.repository.PaymentPersistenceProvider
import io.cloudflight.jems.server.payments.repository.PaymentRepository
import io.cloudflight.jems.server.payments.service.model.ComputedPaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.costoption.combineLumpSumTranslatedValues
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumId
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.lumpsum.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.utils.projectEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

class PaymentPersistenceProviderTest: UnitTest() {

    @RelaxedMockK
    lateinit var paymentRepository: PaymentRepository

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @RelaxedMockK
    lateinit var projectLumpSumRepository: ProjectLumpSumRepository

    @RelaxedMockK
    lateinit var fundRepository: ProgrammeFundRepository

    @InjectMockKs
    lateinit var paymentPersistenceProvider: PaymentPersistenceProvider

    companion object {
        private const val PROJECT_ID = 1L
        private const val PAYMENT_ID = 2L
        private const val LUMP_SUM_ID = 50L
        private const val FUND_ID = 4L
        private const val PARTNER_ID = 5L
        private val dummyCall = createTestCallEntity(10)
        private val account = UserEntity(
            id = 1,
            email = "admin@admin.dev",
            name = "Name",
            surname = "Surname",
            userRole = UserRoleEntity(id = 1, name = "ADMIN"),
            password = "hash_pass",
            userStatus = UserStatus.ACTIVE
        )
        private val dummyProject = ProjectEntity(
            id = PROJECT_ID,
            call = createTestCallEntity(0, name = "Test Call"),
            acronym = "Test Project",
            applicant = dummyCall.creator,
            currentStatus = ProjectStatusHistoryEntity(id = 1, status = ApplicationStatus.DRAFT, user = account),
        )

        private val fund = ProgrammeFundEntity(
            id = FUND_ID,
            selected = true,
            type = ProgrammeFundType.OTHER,
        )

        private val paymentEntity = PaymentToProjectEntity(
            id = PAYMENT_ID,
            project = dummyProject,
            amountApprovedPerFund = BigDecimal(100),
            partnerId = PARTNER_ID,
            fund = fund,
            orderNr = 1,
            programmeLumpSumId = LUMP_SUM_ID
        )
        private val computedPayment = ComputedPaymentToProject(
            projectId = PROJECT_ID,
            amountApprovedPerFund = BigDecimal(100),
            partnerId = PARTNER_ID,
            orderNr = 1,
            programmeLumpSumId = LUMP_SUM_ID,
            programmeFundId = FUND_ID
        )

        private val currentTime = ZonedDateTime.now()

        private val expectedPayments = PaymentToProject(
            paymentId = PAYMENT_ID,
            paymentType = io.cloudflight.jems.api.payments.PaymentType.FTLS,
            projectId = "",
            projectAcronym = "Test Project",
            paymentClaimNo = 0,
            fundName = "OTHER",
            amountApprovedPerFound = BigDecimal(100),
            amountPaidPerFund = BigDecimal.ZERO,
            paymentApprovalDate = currentTime,
            paymentClaimSubmissionDate = null,
            totalEligibleAmount = BigDecimal(10)
        )
        private val projectLumpSumId = ProjectLumpSumId(projectId =dummyProject.id, orderNr = 1)
        val programmeLumpSum = programmeLumpSum(id = 50)
        private val contribution1 = ProjectPartnerLumpSumEntity(
            id = ProjectPartnerLumpSumId(
                projectLumpSumId,
                partner(sortNumber = 1, id = 1)
            ),
            amount = BigDecimal.TEN,
        )
        val lumpSumEntity = ProjectLumpSumEntity(
            id = projectLumpSumId,
            programmeLumpSum = programmeLumpSum,
            endPeriod = 7,
            lumpSumContributions = setOf(contribution1),
            paymentEnabledDate = currentTime,
            lastApprovedVersionBeforeReadyForPayment = "v1.0"
        )

        private fun programmeLumpSum(id: Long) = ProgrammeLumpSumEntity(
            id = id,
            translatedValues = combineLumpSumTranslatedValues(
                programmeLumpSumId = id,
                name = setOf(InputTranslation(SystemLanguage.EN, "")),
                description = emptySet()
            ),
            cost = BigDecimal.TEN,
            splittingAllowed = true,
            phase = ProgrammeLumpSumPhase.Preparation,
            isFastTrack = false
        )

        private fun partner(sortNumber: Int, id: Long) = ProjectPartnerEntity(
            id = id,
            project = dummyProject,
            abbreviation = "",
            role = ProjectPartnerRole.LEAD_PARTNER,
            sortNumber = sortNumber,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
        )
    }

    @Test
    fun getPayments() {
        every { paymentRepository.getAllByGrouping(Pageable.unpaged()) } returns PageImpl(mutableListOf(paymentEntity))
        every { projectLumpSumRepository.getByIdProjectIdAndIdOrderNr(PROJECT_ID, 1) } returns lumpSumEntity

         assertThat(paymentPersistenceProvider.getAllPaymentToProject(Pageable.unpaged()).content)
            .containsAll(listOf(expectedPayments))
    }

    @Test
    fun deleteAllByProjectIdAndOrderNrIn() {
        every { paymentRepository.deleteAllByProjectIdAndOrderNr(PROJECT_ID, setOf(1))} returns listOf(paymentEntity)
        assertThat(paymentPersistenceProvider.deleteAllByProjectIdAndOrderNrIn(PROJECT_ID, setOf(1)))
            .containsAll(listOf(paymentEntity))
    }

    @Test
    fun savePaymentToProjects() {
        every { projectRepository.getById(PROJECT_ID)} returns projectEntity
        every { fundRepository.getById(any())} returns fund
        every { paymentRepository.saveAll(listOf(paymentEntity))} returns mutableListOf(paymentEntity)
       paymentPersistenceProvider.savePaymentToProjects(PROJECT_ID, listOf(computedPayment))
    }
}
