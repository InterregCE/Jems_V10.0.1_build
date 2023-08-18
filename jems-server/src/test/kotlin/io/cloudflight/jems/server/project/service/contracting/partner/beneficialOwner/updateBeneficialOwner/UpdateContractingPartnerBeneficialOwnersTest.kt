package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.updateBeneficialOwner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwnersPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.updateBeneficialOwners.MaxAmountOfBeneficialOwnersReachedException
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.updateBeneficialOwners.UpdateContractingPartnerBeneficialOwners
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateContractingPartnerBeneficialOwnersTest : UnitTest() {

    companion object {
        const val projectId = 1L
        const val partnerId = 20L

        private val beneficialOwner1 = ContractingPartnerBeneficialOwner(
            id = 18L,
            partnerId = partnerId,
            firstName = "Test1",
            lastName = "Sample2",
            vatNumber = "123456",
            birth = null
        )

        private val beneficialOwner2 = ContractingPartnerBeneficialOwner(
            id = 19L,
            partnerId = partnerId,
            firstName = "Test2",
            lastName = "Sample2",
            vatNumber = "102030",
            birth = null
        )

        private val invalidBeneficialOwner = ContractingPartnerBeneficialOwner(
            id = 20L,
            partnerId = partnerId,
            firstName = "Test3",
            lastName = "Sample3",
            vatNumber = "",
            birth = null
        )
    }

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @MockK
    lateinit var beneficialOwnersPersistence: ContractingPartnerBeneficialOwnersPersistence

    @MockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var interactor: UpdateContractingPartnerBeneficialOwners

    @BeforeEach
    fun setup() {
        clearMocks(beneficialOwnersPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
                AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), any(), any()) } returns emptyMap()
        every { generalValidator.notBlank(any(), any()) } returns emptyMap()
    }

    @Test
    fun `update - success`() {
        every { validator.validatePartnerLock(partnerId) } returns Unit
        every {
            beneficialOwnersPersistence
                .updateBeneficialOwners(projectId, partnerId, listOf(beneficialOwner1, beneficialOwner2))
        } returns listOf(beneficialOwner1, beneficialOwner2)

        val owners = listOf(beneficialOwner1, beneficialOwner2)
        Assertions.assertThat(interactor.updateBeneficialOwners(projectId, partnerId, owners))
            .containsExactly(beneficialOwner1, beneficialOwner2)
    }


    @Test
    fun `update - max amount reached`() {
        val owners = mockk<List<ContractingPartnerBeneficialOwner>>()
        every { validator.validatePartnerLock(partnerId) } returns Unit
        every { owners.size } returns 31
        assertThrows<MaxAmountOfBeneficialOwnersReachedException> {
            interactor.updateBeneficialOwners(
                projectId,
                partnerId,
                owners
            )
        }
    }

    @Test
    fun `update - test input validations`() {
        val owners = listOf(beneficialOwner1, beneficialOwner2, invalidBeneficialOwner)
        every { validator.validatePartnerLock(partnerId) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(any()) } throws
                AppInputValidationException(emptyMap())
        every { generalValidator.notBlank(any(), any()) } answers {
            mapOf(secondArg<String>() to I18nMessage(i18nKey = "${firstArg<String>()}---notBlank"))
        }
        assertThrows<AppInputValidationException> { interactor.updateBeneficialOwners(projectId, partnerId, owners) }
    }

    @Test
    fun `update - section locked`() {
        val owners = listOf(beneficialOwner1, beneficialOwner2)
        val exception = ContractingModificationDeniedException()
        every { validator.validatePartnerLock(partnerId)} throws exception

        assertThrows<ContractingModificationDeniedException> {
            interactor.updateBeneficialOwners(projectId, partnerId, owners)
        }
    }
}
