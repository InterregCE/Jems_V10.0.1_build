package io.cloudflight.jems.server.programme.service.costoption.create_unit_cost

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.OfficeAndAdministrationCosts
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory.StaffCosts
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class CreateUnitCostInteractorTest {

    @MockK
    lateinit var persistence: ProgrammeUnitCostPersistence

    @MockK
    lateinit var auditService: AuditService

    private lateinit var createUnitCostInteractor: CreateUnitCostInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        createUnitCostInteractor = CreateUnitCost(persistence, auditService)
    }

    @Test
    fun `create unit cost - invalid`() {
        val wrongUnitCost = ProgrammeUnitCost(
            id = null,
            name = " ",
            description = "test unit cost 1",
            type = "this type is longer than25",
            costPerUnit = null,
            categories = setOf(OfficeAndAdministrationCosts),
        )
        val ex = assertThrows<I18nValidationException> { createUnitCostInteractor.createUnitCost(wrongUnitCost) }
        assertThat(ex.i18nFieldErrors).containsExactlyInAnyOrderEntriesOf(mapOf(
            "name" to I18nFieldError(i18nKey = "programme.unitCost.name.should.not.be.empty"),
            "costPerUnit" to I18nFieldError(i18nKey = "programme.unitCost.costPerUnit.invalid"),
            "type" to I18nFieldError(i18nKey = "programme.unitCost.type.too.long"),
            "categories" to I18nFieldError(i18nKey = "programme.unitCost.categories.min.2"),
        ))
    }

    @Test
    fun `create unit cost - OK`() {
        every { persistence.createUnitCost(any()) } returnsArgument 0
        val unitCost = ProgrammeUnitCost(
            id = null,
            name = "UC1",
            description = "test unit cost 1",
            type = "type 1",
            costPerUnit = BigDecimal.ONE,
            categories = setOf(OfficeAndAdministrationCosts, StaffCosts),
        )
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}

        assertThat(createUnitCostInteractor.createUnitCost(unitCost)).isEqualTo(unitCost.copy())
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_UNIT_COST_ADDED,
            description = "Programme unit cost (id=null) 'UC1' has been added" // null will be real ID from DB sequence
        ))
    }

    @Test
    fun `create unit cost - wrong ID filled in`() {
        val unitCost = ProgrammeUnitCost(
            id = 1L,
            name = "UC1",
            type = "UC1 type",
            costPerUnit = BigDecimal.ONE,
        )

        assertThrows<I18nValidationException>("when creating id cannot be filled in") {
            createUnitCostInteractor.createUnitCost(unitCost) }
    }

}
