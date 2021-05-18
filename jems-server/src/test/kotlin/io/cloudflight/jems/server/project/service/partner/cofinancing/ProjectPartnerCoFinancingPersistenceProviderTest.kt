package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecovery
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundType
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.partner.PartnerIdentityRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerContributionRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerFinancingRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingFundId
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionEntity
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.cofinancing.toCoFinancingModel
import io.cloudflight.jems.server.project.repository.partner.cofinancing.toContributionModel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

open class ProjectPartnerCoFinancingPersistenceProviderTest {
    protected val partnerId = 1L
    protected val projectId = 2L
    protected val timestamp: Timestamp = Timestamp.valueOf(LocalDateTime.now())
    protected val version = "1.0"

    protected class PreviousVersionOfPartner(
        override val language: SystemLanguage?,
        override val id: Long,
        override val projectId: Long,
        override val abbreviation: String,
        override val role: ProjectPartnerRole,
        override val sortNumber: Int,
        override val nameInOriginalLanguage: String?,
        override val nameInEnglish: String?,
        override val partnerType: ProjectTargetGroup?,
        override val vat: String?,
        override val vatRecovery: ProjectPartnerVatRecovery?,
        override val legalStatusId: Long,
        override val department: String?
    ) : PartnerIdentityRow

    protected class PreviousVersionOfCoFinancing(
        override val partnerId: Long,
        override val type: ProjectPartnerCoFinancingFundType,
        override val percentage: BigDecimal,
        override val programmeFund: ProgrammeFundEntity?
    ) : PartnerFinancingRow

    protected class PreviousVersionOfContribution(
        override val id: Long,
        override val partnerId: Long,
        override val name: String?,
        override val status: ProjectPartnerContributionStatus?,
        override val amount: BigDecimal
    ) : PartnerContributionRow

    private val fund = ProgrammeFundEntity(id = 1, selected = true)

    val previousFinances = setOf(
        ProjectPartnerCoFinancingEntity(
            coFinancingFundId = ProjectPartnerCoFinancingFundId(1, ProjectPartnerCoFinancingFundType.MainFund),
            percentage = BigDecimal.valueOf(15.5),
            programmeFund = fund
        ),
        ProjectPartnerCoFinancingEntity(
            coFinancingFundId = ProjectPartnerCoFinancingFundId(1, ProjectPartnerCoFinancingFundType.PartnerContribution),
            percentage = BigDecimal.valueOf(25.5),
            programmeFund = null
        )
    )

    val currentFinances = setOf(
        ProjectPartnerCoFinancingEntity(
            coFinancingFundId = ProjectPartnerCoFinancingFundId(1, ProjectPartnerCoFinancingFundType.MainFund),
            percentage = BigDecimal.valueOf(19.5),
            programmeFund = fund
        ),
        ProjectPartnerCoFinancingEntity(
            coFinancingFundId = ProjectPartnerCoFinancingFundId(1, ProjectPartnerCoFinancingFundType.PartnerContribution),
            percentage = BigDecimal.valueOf(79.5),
            programmeFund = null
        )
    )

    val previousContributions = listOf(
        ProjectPartnerContributionEntity(
            id = 1,
            partnerId = 1,
            name = null,
            status = ProjectPartnerContributionStatus.Public,
            amount = BigDecimal.ONE
        ),
        ProjectPartnerContributionEntity(
            id = 2,
            partnerId = 1,
            name = "BMW",
            status = ProjectPartnerContributionStatus.AutomaticPublic,
            amount = BigDecimal.ZERO
        )
    )

    val currentContributions = listOf(
        ProjectPartnerContributionEntity(
            id = 1,
            partnerId = 1,
            name = null,
            status = ProjectPartnerContributionStatus.Public,
            amount = BigDecimal.TEN
        ),
        ProjectPartnerContributionEntity(
            id = 2,
            partnerId = 1,
            name = "BMW",
            status = ProjectPartnerContributionStatus.AutomaticPublic,
            amount = BigDecimal.ONE
        )
    )

    val currentValue = ProjectPartnerCoFinancingAndContribution(
        finances = currentFinances.toCoFinancingModel(),
        partnerContributions = currentContributions.toContributionModel(),
        partnerAbbreviation = "partner"
    )

    val previousValue = ProjectPartnerCoFinancingAndContribution(
        finances = previousFinances.toCoFinancingModel(),
        partnerContributions = previousContributions.toContributionModel(),
        partnerAbbreviation = "previous partner"
    )

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = ProjectPartnerTestUtil.project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = ProgrammeLegalStatusEntity(id = 1,),
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes,
        financing = currentFinances,
        partnerContributions = currentContributions
    )

    private val previousFinancingValues = listOf (
        PreviousVersionOfCoFinancing(
            partnerId = 1,
            type = ProjectPartnerCoFinancingFundType.MainFund,
            percentage = BigDecimal.valueOf(15.5),
            programmeFund = fund
        ),
        PreviousVersionOfCoFinancing(
            partnerId = 1,
            type = ProjectPartnerCoFinancingFundType.PartnerContribution,
            percentage = BigDecimal.valueOf(25.5),
            programmeFund = null
        )
    )

    private val previousContributionValues = listOf (
        PreviousVersionOfContribution(
            id = 1,
            partnerId = 1,
            name = null,
            status = ProjectPartnerContributionStatus.Public,
            amount = BigDecimal.ONE
        ),
        PreviousVersionOfContribution(
            id = 2,
            partnerId = 1,
            name = "BMW",
            status = ProjectPartnerContributionStatus.AutomaticPublic,
            amount = BigDecimal.ZERO
        )
    )

    private val previousProjectPartner = PreviousVersionOfPartner(
        language = SystemLanguage.EN,
        id = 1,
        projectId = 1,
        abbreviation = "previous partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        sortNumber = 1,
        nameInOriginalLanguage = "",
        nameInEnglish = "",
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes,
        legalStatusId = 1,
        department = ""
    )

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    lateinit var projectVersionUtils: ProjectVersionUtils
    protected lateinit var persistence: ProjectPartnerCoFinancingPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectPartnerCoFinancingPersistenceProvider(
            projectPartnerRepository,
            projectVersionUtils,
            projectPersistence
        )
        every { projectPersistence.getProjectIdForPartner(partnerId) } returns projectId
        every { projectVersionRepo.findTimestampByVersion(projectId, version) } returns timestamp
    }

    @Test
    fun `should return current version of coFinancing`() {
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartner)
        assertThat(persistence.getCoFinancingAndContributions(1, null)).isEqualTo(currentValue)
    }

    @Test
    fun `should return previous version of coFinancing`() {
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.findPartnerIdentityByIdAsOfTimestamp(partnerId, timestamp) } returns listOf(previousProjectPartner)
        every { projectPartnerRepository.findPartnerFinancingByIdAsOfTimestamp(partnerId, timestamp) } returns previousFinancingValues
        every { projectPartnerRepository.findPartnerContributionByIdAsOfTimestamp(partnerId, timestamp) } returns previousContributionValues
        assertThat(persistence.getCoFinancingAndContributions(1, version)).isEqualTo(previousValue)
    }
}