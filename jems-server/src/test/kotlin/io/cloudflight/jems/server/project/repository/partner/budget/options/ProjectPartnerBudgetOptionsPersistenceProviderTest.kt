package io.cloudflight.jems.server.project.repository.partner.budget.options

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.repository.toModel
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptionsEntity
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetUnitCostRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Optional

internal class ProjectPartnerBudgetOptionsPersistenceProviderTest : UnitTest() {

    private val projectId = 1L
    private val partnerId = 1L
    private val partnerIds = setOf(1L, 2L)
    private val version = "1.0"
    private val timestamp: Timestamp = Timestamp.valueOf(LocalDateTime.now())
    private val currentVersionOfBudgetOptionsEntity = ProjectPartnerBudgetOptionsEntity(
        partnerId = partnerId,
        officeAndAdministrationOnStaffCostsFlatRate = 10,
        officeAndAdministrationOnDirectCostsFlatRate = null,
        travelAndAccommodationOnStaffCostsFlatRate = 12,
        staffCostsFlatRate = 13,
        otherCostsOnStaffCostsFlatRate = null
    )
    private val previousVersionOfBudgetOptionsEntity = ProjectPartnerBudgetOptionsEntity(
        partnerId = partnerId,
        officeAndAdministrationOnStaffCostsFlatRate = null,
        officeAndAdministrationOnDirectCostsFlatRate = 10,
        travelAndAccommodationOnStaffCostsFlatRate = 14,
        staffCostsFlatRate = 15,
        otherCostsOnStaffCostsFlatRate = null
    )

    @MockK
    lateinit var projectVersionUtils: ProjectVersionUtils

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var budgetOptionsRepository: ProjectPartnerBudgetOptionsRepository

    @MockK
    lateinit var budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository

    @MockK
    lateinit var budgetTravelRepository: ProjectPartnerBudgetTravelRepository

    @MockK
    lateinit var budgetExternalRepository: ProjectPartnerBudgetExternalRepository

    @MockK
    lateinit var budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository

    @MockK
    lateinit var budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository

    @MockK
    lateinit var budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository

    @MockK
    lateinit var partnerRepository: ProjectPartnerRepository

    @InjectMockKs
    lateinit var persistence: ProjectPartnerBudgetOptionsPersistenceProvider

    @BeforeAll
    fun setup() {
        every {
            projectVersionUtils.fetch<ProjectPartnerBudgetOptions>(version, projectId, any(), any())
        } answers { lastArg<(Timestamp) -> ProjectPartnerBudgetOptions>().invoke(timestamp) }
        every {
            projectVersionUtils.fetch<ProjectPartnerBudgetOptions>(null, projectId, any(), any())
        } answers { thirdArg<() -> ProjectPartnerBudgetOptions>().invoke() }
        // mock to call method for getting current version, historic version of projectId for partnerId
        every {
            projectVersionUtils.fetchProjectId(null, partnerId, any(), any())
        } answers { thirdArg<(Long) -> Long>().invoke(partnerId) }
        every {
            projectVersionUtils.fetchProjectId(version, partnerId, any(), any())
        } answers { lastArg<(Long) -> Long>().invoke(partnerId) }

        every { partnerRepository.getProjectIdForPartner(projectId) } returns partnerId
        every { budgetOptionsRepository.findById(partnerId) } returns Optional.of(currentVersionOfBudgetOptionsEntity)
        every { budgetOptionsRepository.findByPartnerIdAsOfTimestamp(partnerId, timestamp) } returns Optional.of(
            previousVersionOfBudgetOptionsEntity
        )
    }

    @Test
    fun `should return current version of budget options when version is null`() {
        assertThat(persistence.getBudgetOptions(partnerId)).isEqualTo(
            currentVersionOfBudgetOptionsEntity.toProjectPartnerBudgetOptions()
        )
        verify { budgetOptionsRepository.findById(partnerId) }
    }

    @Test
    fun `should return previous version of budget options when version is not null`() {
        every { partnerRepository.getProjectIdByPartnerIdInFullHistory(partnerId) } returns 1
        assertThat(persistence.getBudgetOptions(partnerId, version)).isEqualTo(
            previousVersionOfBudgetOptionsEntity.toProjectPartnerBudgetOptions()
        )
        verify { budgetOptionsRepository.findByPartnerIdAsOfTimestamp(partnerId, timestamp) }
    }

    @Test
    fun `should return list of budget options for provided partnerIds`() {
        every { budgetOptionsRepository.findAllById(partnerIds) } returns listOf(currentVersionOfBudgetOptionsEntity)
        every {
            projectVersionUtils.fetch<List<ProjectPartnerBudgetOptions>>(null, projectId, any(), any())
        } answers { thirdArg<() -> List<ProjectPartnerBudgetOptions>>().invoke() }
        assertThat(persistence.getBudgetOptions(partnerIds, projectId)).isEqualTo(
            listOf(currentVersionOfBudgetOptionsEntity).toProjectPartnerBudgetOptions()
        )
        verify { budgetOptionsRepository.findAllById(partnerIds) }
    }

    @Test
    fun `should update budget options`() {
        val budgetOptions = ProjectPartnerBudgetOptions(partnerId, 10, null, 12, 30, null)
        every { budgetOptionsRepository.save(budgetOptions.toProjectPartnerBudgetOptionsEntity()) } returnsArgument 0
        assertThat(persistence.updateBudgetOptions(partnerId, budgetOptions)).isEqualTo(Unit)
        verify { budgetOptionsRepository.save(budgetOptions.toProjectPartnerBudgetOptionsEntity()) }
    }

    @Test
    fun `should delete budget options when it exists`() {
        every { budgetOptionsRepository.existsById(partnerId) } returns true
        every { budgetOptionsRepository.deleteById(partnerId) } returns Unit
        assertThat(persistence.deleteBudgetOptions(partnerId)).isEqualTo(Unit)
        verifyOrder {
            budgetOptionsRepository.existsById(partnerId)
            budgetOptionsRepository.deleteById(partnerId)
        }
    }

    @Test
    fun `should do noting for deleting budget options when it does not exist`() {
        every { budgetOptionsRepository.existsById(partnerId) } returns false
        assertThat(persistence.deleteBudgetOptions(partnerId)).isEqualTo(Unit)
        verify { budgetOptionsRepository.existsById(partnerId) }
    }

    @Test
    fun `should return flat rates of project's call`() {
        val projectPartnerEntityMock: ProjectPartnerEntity = mockk()
        val projectEntityMock: ProjectEntity = mockk()
        val callEntityMock: CallEntity = mockk()
        val flatRate =
            ProjectCallFlatRateEntity(FlatRateSetupId(callEntityMock, FlatRateType.OTHER_COSTS_ON_STAFF_COSTS), 12, false)
        every { projectPartnerEntityMock.project } returns projectEntityMock
        every { projectEntityMock.call } returns callEntityMock
        every { callEntityMock.flatRates } returns mutableSetOf(flatRate)

        every { partnerRepository.getById(partnerId) } returns projectPartnerEntityMock
        assertThat(persistence.getProjectCallFlatRateByPartnerId(partnerId)).isEqualTo(mutableSetOf(flatRate).toModel())
        verify { partnerRepository.getById(partnerId) }
    }

    @TestFactory
    fun `should delete budget costs`() =
        listOf(
            Triple("staff costs", budgetStaffCostRepository, persistence::deleteStaffCosts),
            Triple("external costs", budgetExternalRepository, persistence::deleteExternalCosts),
            Triple("equipment costs", budgetEquipmentRepository, persistence::deleteEquipmentCosts),
            Triple("infrastructure costs", budgetInfrastructureRepository, persistence::deleteInfrastructureCosts),
            Triple("travel costs", budgetTravelRepository, persistence::deleteTravelAndAccommodationCosts),
            Triple("unit costs", budgetUnitCostRepository, persistence::deleteUnitCosts),
        ).map {
            DynamicTest.dynamicTest(
                "should delete budget ${it.first}"
            ) {
                every { it.second.deleteAllByBasePropertiesPartnerId(partnerId) } returns Unit
                assertThat(it.third.invoke(partnerId)).isEqualTo(Unit)
                verify { it.second.deleteAllByBasePropertiesPartnerId(partnerId) }
            }
        }

}
