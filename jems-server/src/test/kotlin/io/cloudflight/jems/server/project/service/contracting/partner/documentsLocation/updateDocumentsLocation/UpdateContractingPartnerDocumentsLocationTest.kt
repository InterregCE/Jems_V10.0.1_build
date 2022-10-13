package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.updateDocumentsLocation

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocationPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateContractingPartnerDocumentsLocationTest : UnitTest() {

    companion object {
        const val partnerId = 20L
        const val projectId = 1L

        private val documentsLocation = ContractingPartnerDocumentsLocation(
            id = 18L,
            partnerId = partnerId,
            firstName = "Test",
            lastName = "Sample",
            nutsThreeRegionCode = "",
            city = "Istanbul",
            countryCode = "TR",
            nutsTwoRegionCode = "",
            country = "Turkey",
            emailAddress = "sample@mail.com",
            locationNumber = "12A",
            homepage = "homepage",
            institutionName = "Sample name",
            nutsThreeRegion = "",
            nutsTwoRegion = "",
            postalCode = "34000",
            street = "Sample street",
            telephoneNo = "1020304050",
            title = "Title"
        )

        private val invalidDocumentsLocation = ContractingPartnerDocumentsLocation(
            id = 19L,
            partnerId = partnerId,
            firstName = "Test",
            lastName = "Sample",
            nutsThreeRegionCode = "",
            city = "Istanbul",
            countryCode = "TR",
            nutsTwoRegionCode = "",
            country = "Turkey",
            emailAddress = "invalid-mail.com",
            locationNumber = "12A",
            homepage = "",
            institutionName = "",
            nutsThreeRegion = "",
            nutsTwoRegion = "",
            postalCode = "",
            street = "",
            telephoneNo = "",
            title = ""
        )
    }

    @MockK
    lateinit var documentsLocationPersistence: ContractingPartnerDocumentsLocationPersistence

    @InjectMockKs
    lateinit var interactor: UpdateContractingPartnerDocumentsLocation

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @BeforeEach
    fun setup() {
        clearMocks(documentsLocationPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
                AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), any(), any()) } returns emptyMap()
        every { generalValidator.matches(any(), any(), any(), any()) } returns emptyMap()
    }

    @Test
    fun `update documents location - success`() {
        every {
            documentsLocationPersistence
                .updateDocumentsLocation(projectId, partnerId, documentsLocation)
        } returns documentsLocation

        Assertions.assertThat(interactor.updateDocumentsLocation(projectId, partnerId, documentsLocation))
            .isEqualTo(documentsLocation)
    }

    @Test
    fun `update - test input validations`() {
        every { generalValidator.throwIfAnyIsInvalid(any()) } throws
                AppInputValidationException(emptyMap())
        every { generalValidator.matches(any<String>(), any(), any(), any()) } answers {
            mapOf(thirdArg<String>() to I18nMessage(i18nKey = "${firstArg<String>()}---maxLength-${secondArg<String>()}"))
        }
        assertThrows<AppInputValidationException> {
            interactor.updateDocumentsLocation(
                projectId,
                partnerId,
                invalidDocumentsLocation
            )
        }
    }
}