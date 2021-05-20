package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressType
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddress
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.repository.partner.toOutputProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CreateProjectPartnerInteractorTest: UnitTest() {
    @MockK
    lateinit var persistence: PartnerPersistence

    @InjectMockKs
    lateinit var createInteractor: CreateProjectPartner

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = ProjectPartnerTestUtil.project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = ProgrammeLegalStatusEntity(id = 1,),
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes
    )
    private val partnerTranslatedValues =
        mutableSetOf(ProjectPartnerTranslEntity(TranslationPartnerId(1, SystemLanguage.EN), "test"))
    private val legalStatus = ProgrammeLegalStatusEntity(id = 1)
    private val partnerAddress = ProjectPartnerAddress(
        addressId = ProjectPartnerAddressId(1, ProjectPartnerAddressType.Organization),
        address = AddressEntity("AT")
    )
    private val projectPartnerWithOrganization = ProjectPartnerEntity(
        id = 1,
        project = ProjectPartnerTestUtil.project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        translatedValues = partnerTranslatedValues,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = legalStatus,
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes,
        addresses = setOf(partnerAddress)
    )

    private val outputProjectPartnerDetail = projectPartner.toOutputProjectPartnerDetail()

    @Test
    fun createProjectPartner() {
        val inputProjectPartner =
            InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        every { persistence.create(1, inputProjectPartner) } returns outputProjectPartnerDetail

        assertThat(createInteractor.create(1, inputProjectPartner)).isEqualTo(outputProjectPartnerDetail)
    }

    @Test
    fun `createProjectPartner not existing`() {
        val inputProjectPartner = InputProjectPartnerCreate(
            "partner", ProjectPartnerRole.LEAD_PARTNER, null, "test", "test", setOf(
                InputTranslation(
                    SystemLanguage.EN, "test"
                )
            ), ProjectTargetGroup.BusinessSupportOrganisation,
            1, "test vat", ProjectPartnerVatRecovery.Yes
        )
        every { persistence.create(-1, inputProjectPartner) } throws ResourceNotFoundException("project")
        val ex = assertThrows<ResourceNotFoundException> { createInteractor.create(-1, inputProjectPartner) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun createProjectPartnerWithOrganization() {
        val inputProjectPartner = InputProjectPartnerCreate(
            "partner", ProjectPartnerRole.LEAD_PARTNER, null, "test", "test", setOf(
                InputTranslation(
                    SystemLanguage.EN, "test"
                )
            ), legalStatusId = 1
        )
        every { persistence.create(1, inputProjectPartner) } returns projectPartnerWithOrganization.toOutputProjectPartnerDetail()
        assertThat(
            createInteractor.create(
                1,
                inputProjectPartner
            )
        ).isEqualTo(
            OutputProjectPartnerDetail(
                id = projectPartnerWithOrganization.id,
                abbreviation = projectPartnerWithOrganization.abbreviation,
                role = projectPartnerWithOrganization.role,
                sortNumber = projectPartnerWithOrganization.sortNumber,
                nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
                nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
                department = setOf(InputTranslation(SystemLanguage.EN, "test")),
                partnerType = projectPartnerWithOrganization.partnerType,
                vat = projectPartnerWithOrganization.vat,
                vatRecovery = projectPartnerWithOrganization.vatRecovery,
                legalStatusId = projectPartnerWithOrganization.legalStatus.id,
                addresses = listOf(ProjectPartnerAddressDTO(
                    type = partnerAddress.addressId.type,
                    country = partnerAddress.address.country
                ))
            )
        )
    }
}
