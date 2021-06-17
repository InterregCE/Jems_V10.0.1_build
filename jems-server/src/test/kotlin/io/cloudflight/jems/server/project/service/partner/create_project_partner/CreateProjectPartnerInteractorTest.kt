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
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddress
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.toOutputProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import java.util.Optional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus

internal class CreateProjectPartnerInteractorTest: UnitTest() {
    @MockK
    lateinit var persistence: PartnerPersistence

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @InjectMockKs
    lateinit var createInteractor: CreateProjectPartner

    val generalValidator: GeneralValidatorService = GeneralValidatorDefaultImpl()

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

    private val projectPartnerInclTransl =
        projectPartner.copy(translatedValues = partnerTranslatedValues)

    private val outputProjectPartnerDetail = projectPartner.toOutputProjectPartnerDetail()

    @Test
    fun createProjectPartner() {
        val inputProjectPartner =
            InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        every { persistence.create(1, inputProjectPartner) } returns outputProjectPartnerDetail
        every { projectPartnerRepository.countByProjectId(1) } returns 0
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.empty()


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
        every { projectPartnerRepository.countByProjectId(-1) } returns 0
        every { projectPartnerRepository.findFirstByProjectIdAndRole(-1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(-1, "partner") } returns Optional.empty()
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

    @Test
    fun `error on already existing partner name when creating`() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val inputProjectPartner2 = InputProjectPartnerCreate("partner", ProjectPartnerRole.PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(0,
            ProjectPartnerTestUtil.project, inputProjectPartner.abbreviation!!, inputProjectPartner.role!!, legalStatus = legalStatus)

        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.of(projectPartnerWithProject)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 0

        val ex = assertThrows<PartnerAbbreviationNotUnique> {
            createInteractor.create(1, inputProjectPartner2)
        }

        assertThat(ex.i18nMessage.i18nKey).isEqualTo("use.case.create.project.partner.abbreviation.not.unique")
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `error on multiple LEAD_PARTNER partner creation attempt`() {
        val inputProjectPartnerLead =
            InputProjectPartnerCreate("partnerLead", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)

        every { persistence.create(1, inputProjectPartnerLead) } returns outputProjectPartnerDetail
        every { projectPartnerRepository.countByProjectId(1) } returns 1
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.of(projectPartner)
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.empty()

        assertThrows<LeadPartnerAlreadyExists> { createInteractor.create(1, inputProjectPartnerLead) }
    }

    @Test
    fun `error createProjectPartner when MAX count exceeded`() {
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 30

        val ex = assertThrows<MaximumNumberOfPartnersReached> {
            createInteractor.create(
                1, InputProjectPartnerCreate(
                    "partner", ProjectPartnerRole.PARTNER, null, "test", "test", setOf(
                        InputTranslation(
                            SystemLanguage.EN, "test"
                        )
                    ), ProjectTargetGroup.BusinessSupportOrganisation,
                    1, "test vat", ProjectPartnerVatRecovery.Yes
                )
            )
        }
        assertThat(ex.i18nMessage.i18nKey).isEqualTo("use.case.create.project.partner.max.allowed.count.reached")
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
