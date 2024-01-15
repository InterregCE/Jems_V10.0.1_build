package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.call.entity.CallFundRateEntity
import io.cloudflight.jems.server.call.entity.FundSetupId
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationEntity
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.partner.PartnerIdentityRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerContributionRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerFinancingRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingFundId
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingSpfEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionSpfEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.budget.cofinancing.ProjectPartnerCoFinancingRepository
import io.cloudflight.jems.server.project.repository.budget.cofinancing.ProjectPartnerContributionSpfRepository
import io.cloudflight.jems.server.project.repository.budget.cofinancing.ProjectPartnerSpfCoFinancingRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.cofinancing.toCoFinancingModel
import io.cloudflight.jems.server.project.repository.partner.cofinancing.toContributionModel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Optional

open class ProjectPartnerCoFinancingPersistenceProviderTest {
    protected val partnerId = 1L
    protected val projectId = 2L
    protected val timestamp: Timestamp = Timestamp.valueOf(LocalDateTime.now())
    protected val version = "1.0"

    protected class PreviousVersionOfPartner(
        override val language: SystemLanguage?,
        override val id: Long,
        override val active: Boolean,
        override val projectId: Long,
        override val abbreviation: String,
        override val role: ProjectPartnerRole,
        override val createdAt: Timestamp,
        override val sortNumber: Int,
        override val nameInOriginalLanguage: String?,
        override val nameInEnglish: String?,
        override val partnerType: ProjectTargetGroup?,
        override val partnerSubType: PartnerSubType?,
        override val nace: NaceGroupLevel?,
        override val otherIdentifierNumber: String?,
        override val otherIdentifierDescription: String?,
        override val pic: String?,
        override val vat: String?,
        override val vatRecovery: ProjectPartnerVatRecovery?,
        override val legalStatusId: Long,
        override val department: String?
    ) : PartnerIdentityRow

    protected class PreviousVersionOfCoFinancing(
        override val orderNr: Int,
        override val percentage: BigDecimal,
        override val language: SystemLanguage?,
        override val fundId: Long?,
        override val selected: Boolean?,
        override val fundType: String?,
        override val abbreviation: String?,
        override val description: String?,
    ) : PartnerFinancingRow

    protected class PreviousVersionOfContribution(
        override val id: Long,
        override val name: String?,
        override val status: ProjectPartnerContributionStatus?,
        override val amount: BigDecimal
    ) : PartnerContributionRow

    private val fund = ProgrammeFundEntity(id = 1, selected = true)

    private val currentFinances = mutableListOf(
        ProjectPartnerCoFinancingEntity(
            coFinancingFundId = ProjectPartnerCoFinancingFundId(1, 1),
            percentage = BigDecimal.valueOf(19.5),
            programmeFund = fund
        ),
        ProjectPartnerCoFinancingEntity(
            coFinancingFundId = ProjectPartnerCoFinancingFundId(1, 2),
            percentage = BigDecimal.valueOf(79.5),
            programmeFund = null
        )
    )

    private val previousContributions = listOf(
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

    private val currentContributions = listOf(
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

    private val currentValue = ProjectPartnerCoFinancingAndContribution(
        finances = currentFinances.toCoFinancingModel(),
        partnerContributions = currentContributions.toContributionModel("no this cannot be done like this but ok :D"),
        partnerAbbreviation = "no this cannot be done like this but ok :D"
    )

    private val previousValue = ProjectPartnerCoFinancingAndContribution(
        finances = listOf(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = ProgrammeFund(1, true, ProgrammeFundType.ERDF, emptySet(), emptySet()),
                percentage =  BigDecimal.valueOf(15.5),
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                percentage = BigDecimal.valueOf(25.5),
            ),
        ),
        partnerContributions = previousContributions.toContributionModel("no this is not testing mapper, but ok"),
        partnerAbbreviation = "no this is not testing mapper, but ok"
    )

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = ProjectPartnerTestUtil.project,
        abbreviation = "no this cannot be done like this but ok :D",
        role = ProjectPartnerRole.LEAD_PARTNER,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = ProgrammeLegalStatusEntity(id = 1),
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes,
        partnerContributions = currentContributions
    )

    private val previousFinancingValues = listOf(
        PreviousVersionOfCoFinancing(
            orderNr = 1,
            percentage = BigDecimal.valueOf(15.5),
            language = null,
            abbreviation = null,
            description = null,
            fundId = 1,
            fundType = ProgrammeFundType.ERDF.name,
            selected = true
        ),
        PreviousVersionOfCoFinancing(
            orderNr = 2,
            percentage = BigDecimal.valueOf(25.5),
            language = null,
            abbreviation = null,
            description = null,
            fundId = null,
            fundType = null,
            selected = true
        )
    )

    private val previousContributionValues = listOf(
        PreviousVersionOfContribution(
            id = 1,
            name = null,
            status = ProjectPartnerContributionStatus.Public,
            amount = BigDecimal.ONE
        ),
        PreviousVersionOfContribution(
            id = 2,
            name = "BMW",
            status = ProjectPartnerContributionStatus.AutomaticPublic,
            amount = BigDecimal.ZERO
        )
    )

    private val previousProjectPartner = PreviousVersionOfPartner(
        language = SystemLanguage.EN,
        id = 1,
        active = true,
        projectId = 1,
        abbreviation = "no this is not testing mapper, but ok",
        createdAt = Timestamp.valueOf(LocalDateTime.now()),
        role = ProjectPartnerRole.LEAD_PARTNER,
        sortNumber = 1,
        nameInOriginalLanguage = "",
        nameInEnglish = "",
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
        nace= NaceGroupLevel.A,
        otherIdentifierNumber= "32",
        otherIdentifierDescription = "desc",
        pic= "123",
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes,
        legalStatusId = 1,
        department = ""
    )

    private val mockPartner = mockk<ProjectPartnerEntity>()
    private val callFundRate = CallFundRateEntity(
        setupId = FundSetupId(
            call = ProjectPartnerTestUtil.call,
            programmeFund = ProgrammeFundEntity(
                id = 1,
                selected = true,
                type = ProgrammeFundType.OTHER
            )
        ),
        rate = BigDecimal.TEN,
        isAdjustable = true
    ).apply { this.setupId.programmeFund.translatedValues.add(
        ProgrammeFundTranslationEntity(
            translationId = TranslationId(this.setupId.programmeFund, SystemLanguage.EN),
            abbreviation = "abbreviation",
            description = "description"
        ))
    }
    private val spfFinanceEntity = ProjectPartnerCoFinancingSpfEntity(
        coFinancingFundId = ProjectPartnerCoFinancingFundId(partnerId, 1),
        percentage = BigDecimal.valueOf(30.5),
        programmeFund = callFundRate.setupId.programmeFund
    )
    private val spfContributionEntity = ProjectPartnerContributionSpfEntity(
        id = 2,
        partnerId = 1,
        name = "name",
        status = ProjectPartnerContributionStatus.Public,
        amount = BigDecimal.valueOf(20.5)
    )
    private val programmeFund = ProgrammeFund(
        id = 1,
        selected = true,
        type = ProgrammeFundType.OTHER,
        abbreviation = setOf(InputTranslation(SystemLanguage.EN, "abbreviation")),
        description = setOf(InputTranslation(SystemLanguage.EN, "description"))
    )

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var projectPartnerCoFinancingRepository: ProjectPartnerCoFinancingRepository
    @MockK
    lateinit var projectPartnerSpfCoFinancingRepository: ProjectPartnerSpfCoFinancingRepository
    @MockK
    lateinit var  projectPartnerContributionSpfRepository: ProjectPartnerContributionSpfRepository

    lateinit var projectVersionUtils: ProjectVersionUtils
    protected lateinit var persistence: ProjectPartnerCoFinancingPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectPartnerCoFinancingPersistenceProvider(
            projectPartnerRepository,
            projectPartnerCoFinancingRepository,
            projectPartnerSpfCoFinancingRepository,
            projectPartnerContributionSpfRepository,
            projectVersionUtils,
            projectRepository
        )
        every { projectPartnerRepository.getProjectIdForPartner(partnerId) } returns projectId
        every { projectVersionRepo.findTimestampByVersion(projectId, version) } returns timestamp
    }

    @Test
    fun `should return current version of coFinancing`() {
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartner)
        every { projectPartnerCoFinancingRepository.findAllByCoFinancingFundIdPartnerId(partnerId) } returns currentFinances
        assertThat(persistence.getCoFinancingAndContributions(1, null)).isEqualTo(currentValue)
    }

    @Test
    fun `should return previous version of coFinancing`() {
        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(partnerId) } returns projectId
        every { projectPartnerRepository.findPartnerIdentityByIdAsOfTimestamp(partnerId, timestamp) } returns listOf(
            previousProjectPartner
        )
        every {
            projectPartnerCoFinancingRepository.findPartnerFinancingByIdAsOfTimestamp(
                partnerId,
                timestamp
            )
        } returns previousFinancingValues
        every {
            projectPartnerRepository.findPartnerContributionByIdAsOfTimestamp(
                partnerId,
                timestamp
            )
        } returns previousContributionValues
        assertThat(persistence.getCoFinancingAndContributions(partnerId, version)).isEqualTo(previousValue)
    }

    @Test
    fun `should return current version of SPF coFinancing`() {
        every { projectPartnerSpfCoFinancingRepository.findAllByCoFinancingFundIdPartnerId(partnerId) } returns mutableListOf(spfFinanceEntity)
        every { projectPartnerContributionSpfRepository.findAllByPartnerId(partnerId) } returns mutableListOf(spfContributionEntity)

        val result = persistence.getSpfCoFinancingAndContributions(partnerId, null)
        assertThat(result.finances).containsExactly(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = programmeFund,
                percentage =  BigDecimal.valueOf(30.5)
            )
        )
        assertThat(result.partnerContributions).containsExactly(
            ProjectPartnerContributionSpf(
                id = 2,
                name = "name",
                status = ProjectPartnerContributionStatus.Public,
                amount = BigDecimal.valueOf(20.5)
            )
        )
    }

    @Test
    fun `should return previous version of SPF coFinancing`() {
        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(partnerId) } returns projectId
        every { projectPartnerRepository.findPartnerIdentityByIdAsOfTimestamp(partnerId, timestamp) } returns listOf(
            previousProjectPartner
        )
        val previousSpfFinance = PreviousVersionOfCoFinancing(
            orderNr = 1,
            percentage = BigDecimal.valueOf(30.5),
            language = SystemLanguage.EN,
            abbreviation = "abbreviation",
            description = "description",
            fundId = 1,
            fundType = "Other",
            selected = true
        )
        val previousSpfContributionValue = PreviousVersionOfContribution(
            id = 2,
            name = "name",
            status = ProjectPartnerContributionStatus.Public,
            amount = BigDecimal.valueOf(20.5)
        )
        every { projectPartnerSpfCoFinancingRepository
            .findPartnerFinancingByIdAsOfTimestamp(partnerId, timestamp) } returns mutableListOf(previousSpfFinance)
        every { projectPartnerContributionSpfRepository
            .findPartnerContributionSpfByIdAsOfTimestamp(partnerId, timestamp) } returns mutableListOf(previousSpfContributionValue)

        val result = persistence.getSpfCoFinancingAndContributions(partnerId, version)
        assertThat(result.finances).containsExactly(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = programmeFund,
                percentage =  BigDecimal.valueOf(30.5)
            )
        )
        assertThat(result.partnerContributions).containsExactly(
            ProjectPartnerContributionSpf(
                id = 2,
                name = "name",
                status = ProjectPartnerContributionStatus.Public,
                amount = BigDecimal.valueOf(20.5)
            )
        )
    }

    @Test
    fun `should update SPF coFinancing`() {
        val existingContributions = mutableListOf(
            ProjectPartnerContributionSpfEntity(
                id = 1,
                partnerId = 1,
                name = "name one",
                status = ProjectPartnerContributionStatus.Private,
                amount = BigDecimal.valueOf(10)
            ),
            ProjectPartnerContributionSpfEntity(
                id = 2,
                partnerId = 1,
                name = "name two",
                status = ProjectPartnerContributionStatus.Public,
                amount = BigDecimal.valueOf(20.5)
            )
        )

        val newUpdatedContributions = mutableListOf(
            ProjectPartnerContributionSpfEntity(
                id = 2,
                partnerId = 1,
                name = "name two",
                status = ProjectPartnerContributionStatus.Public,
                amount = BigDecimal.valueOf(90)
            ),
            ProjectPartnerContributionSpfEntity(
                id = 3,
                partnerId = 1,
                name = "name three",
                status = ProjectPartnerContributionStatus.AutomaticPublic,
                amount = BigDecimal.valueOf(200)
            )
        )

        every { mockPartner.project.call.funds } returns mutableSetOf(callFundRate)
        every { projectPartnerRepository
            .findById(partnerId) } returns Optional.of(mockPartner)
        val spfFinance = UpdateProjectPartnerCoFinancing(
            fundId = 1,
            percentage = BigDecimal.valueOf(30.5)
        )
        val spfContributionsUpdate = listOf(
            ProjectPartnerContributionSpf(
                id = 2,
                name = "name two",
                status = ProjectPartnerContributionStatus.Public,
                amount = BigDecimal.valueOf(90)
            ),
            ProjectPartnerContributionSpf(
                id = 0,
                name = "name three",
                status = ProjectPartnerContributionStatus.AutomaticPublic,
                amount = BigDecimal.valueOf(200)
            ),
        )

        val newEntity = ProjectPartnerContributionSpfEntity(
            id = 3,
            partnerId = 1,
            name = "name three",
            status = ProjectPartnerContributionStatus.AutomaticPublic,
            amount = BigDecimal.valueOf(200)
        )


        every { projectPartnerContributionSpfRepository.findAllByPartnerId(partnerId) } returns existingContributions andThen newUpdatedContributions

        val deletedIdsSlot = slot<List<Long>>()
        every { projectPartnerContributionSpfRepository.deleteAllById(capture(deletedIdsSlot)) } returns Unit

        val contributionToSaveSlot = slot<List<ProjectPartnerContributionSpfEntity>>()
        every { projectPartnerContributionSpfRepository
            .saveAll(capture(contributionToSaveSlot)) } returns listOf(newEntity)


        every { projectPartnerSpfCoFinancingRepository.deleteByCoFinancingFundIdPartnerId(partnerId) } returns Unit
        val capturedEntity = slot<MutableSet<ProjectPartnerCoFinancingSpfEntity>>()
        every { projectPartnerSpfCoFinancingRepository.saveAll(capture(capturedEntity)) } returns listOf(spfFinanceEntity)

        val result = persistence.updateSpfCoFinancingAndContribution(partnerId, listOf(spfFinance), spfContributionsUpdate)
        assertThat(result.finances).containsExactly(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = programmeFund,
                percentage =  BigDecimal.valueOf(30.5)
            )
        )

        assertThat(deletedIdsSlot.captured).isEqualTo(listOf(1L))
        assertThat(contributionToSaveSlot.captured).isEqualTo(
            listOf(
                ProjectPartnerContributionSpfEntity(
                    id = 0,
                    partnerId = 1,
                    name = "name three",
                    status = ProjectPartnerContributionStatus.AutomaticPublic,
                    amount = BigDecimal.valueOf(200)
                )
            )
        )

        assertThat(result.partnerContributions).containsExactly(
            ProjectPartnerContributionSpf(
                id = 2,
                name = "name two",
                status = ProjectPartnerContributionStatus.Public,
                amount = BigDecimal.valueOf(90)
            ),
            ProjectPartnerContributionSpf(
                id = 3,
                name = "name three",
                status = ProjectPartnerContributionStatus.AutomaticPublic,
                amount = BigDecimal.valueOf(200)
            ),
        )
    }
}
