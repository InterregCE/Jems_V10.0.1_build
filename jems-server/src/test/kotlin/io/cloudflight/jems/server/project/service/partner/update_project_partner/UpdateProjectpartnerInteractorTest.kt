package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectContactType
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.toEntity
import io.cloudflight.jems.server.project.repository.partner.toOutputProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import java.util.Optional
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus

internal class UpdateProjectpartnerInteractorTest: UnitTest() {
    @MockK
    lateinit var persistence: PartnerPersistence

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @InjectMockKs
    lateinit var updateInteractor: UpdateProjectPartner

    val generalValidator: GeneralValidatorService = GeneralValidatorDefaultImpl()

    private val partnerTranslatedValues =
        mutableSetOf(ProjectPartnerTranslEntity(TranslationPartnerId(1, SystemLanguage.EN), "test"))
    private val laegalStatus = ProgrammeLegalStatusEntity(id = 1)
    private val projectPartnerWithOrganization = ProjectPartnerEntity(
        id = 1,
        project = ProjectPartnerTestUtil.project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        nameInOriginalLanguage = "test",
        nameInEnglish = "test",
        translatedValues = partnerTranslatedValues,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = laegalStatus,
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes
    )
    private val legalStatus = ProgrammeLegalStatusEntity(id = 1)

    @Test
    fun updateProjectPartner() {
        val projectPartnerUpdate =
            InputProjectPartnerUpdate(1, "updated", ProjectPartnerRole.PARTNER, legalStatusId = 1)
        val updatedProjectPartner = ProjectPartnerEntity(
            1,
            ProjectPartnerTestUtil.project,
            projectPartnerUpdate.abbreviation!!,
            projectPartnerUpdate.role!!,
            legalStatus = legalStatus
        )
        every { persistence.update(projectPartnerUpdate) } returns updatedProjectPartner.toOutputProjectPartnerDetail()
        every { projectPartnerRepository.findById(1) } returns Optional.of(updatedProjectPartner)
        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.PARTNER) } returns Optional.of(updatedProjectPartner)
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "updated") } returns Optional.of(updatedProjectPartner)


        Assertions.assertThat(updateInteractor.update(projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun updatePartnerContact() {
        val projectPartnerContactUpdate = InputProjectContact(
            ProjectContactType.ContactPerson,
            "test",
            "test",
            "test",
            "test@ems.eu",
            "test"
        )
        val projectPartner = ProjectPartnerEntity(
            1,
            ProjectPartnerTestUtil.project,
            "updated",
            ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool
        )
        val contactPersonsEntity = setOf(projectPartnerContactUpdate.toEntity(projectPartner))
        val updatedProjectPartner = projectPartner.copy(contacts = contactPersonsEntity)

        every { persistence.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)) } returns updatedProjectPartner.toOutputProjectPartnerDetail()

        Assertions.assertThat(updateInteractor.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun updatePartnerContact_notExisting() {
        val projectPartnerContactUpdate = InputProjectContact(
            ProjectContactType.LegalRepresentative,
            "test",
            "test",
            "test",
            "test@ems.eu",
            "test"
        )
        val contactPersonsDto = setOf(projectPartnerContactUpdate)
        every { persistence.updatePartnerContacts(-1, contactPersonsDto) } throws ResourceNotFoundException("projectPartner")
        val exception = assertThrows<ResourceNotFoundException> {
            updateInteractor.updatePartnerContacts(
                -1,
                contactPersonsDto
            )
        }
        Assertions.assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun updatePartnerMotivation() {
        val projectPartnerMotivationUpdate = ProjectPartnerMotivationDTO(
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test"))
        )
        val projectPartner = ProjectPartnerEntity(
            1,
            ProjectPartnerTestUtil.project,
            "updated",
            ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatusEntity(id = 1),
            partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool
        )
        val updatedProjectPartner =
            projectPartner.copy(motivation = projectPartnerMotivationUpdate.toEntity(projectPartner.id))

        every { updateInteractor.updatePartnerMotivation(1, projectPartnerMotivationUpdate) } returns updatedProjectPartner.toOutputProjectPartnerDetail()

        Assertions.assertThat(updateInteractor.updatePartnerMotivation(1, projectPartnerMotivationUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun updatePartnerContribution_notExisting() {
        val projectPartnerContributionUpdate = ProjectPartnerMotivationDTO(
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test"))
        )
        every { persistence.updatePartnerMotivation(-1, projectPartnerContributionUpdate) } throws ResourceNotFoundException("projectPartner")
        val exception = assertThrows<ResourceNotFoundException> {
            updateInteractor.updatePartnerMotivation(
                -1,
                projectPartnerContributionUpdate
            )
        }
        Assertions.assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun updateProjectPartnerWithOrganization() {
        val projectPartnerUpdate = InputProjectPartnerUpdate(
            1, "updated", ProjectPartnerRole.PARTNER, null, "test", "test", setOf(
                InputTranslation(
                    SystemLanguage.EN, "test"
                )
            ), legalStatusId = 1
        )
        val updatedProjectPartner = ProjectPartnerEntity(
            id = 1,
            project = ProjectPartnerTestUtil.project,
            abbreviation = projectPartnerUpdate.abbreviation!!,
            role = projectPartnerUpdate.role!!,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            translatedValues = projectPartnerWithOrganization.translatedValues,
            legalStatus = legalStatus
        )
        every { updateInteractor.update(projectPartnerUpdate) } returns updatedProjectPartner.toOutputProjectPartnerDetail()
        every { projectPartnerRepository.findById(1) } returns Optional.of(updatedProjectPartner)

        Assertions.assertThat(updateInteractor.update(projectPartnerUpdate))
            .isEqualTo(updatedProjectPartner.toOutputProjectPartnerDetail())
    }

    @Test
    fun `error on already existing partner name when updating`() {
        val inputProjectPartner = InputProjectPartnerCreate("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val updateProjectPartner = InputProjectPartnerUpdate(1, "partner", ProjectPartnerRole.PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(0,
            ProjectPartnerTestUtil.project, inputProjectPartner.abbreviation!!, inputProjectPartner.role!!, legalStatus = legalStatus)
        val oldProjectPartnerWithProject = ProjectPartnerEntity(0,
            ProjectPartnerTestUtil.project, "old partner", inputProjectPartner.role!!, legalStatus = legalStatus)

        every { projectPartnerRepository.findById(1) } returns Optional.of(oldProjectPartnerWithProject)
        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.of(projectPartnerWithProject)
        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 1

        val ex = assertThrows<PartnerAbbreviationNotUnique> {
            updateInteractor.update(updateProjectPartner)
        }

        Assertions.assertThat(ex.i18nMessage.i18nKey).isEqualTo("use.case.update.project.partner.abbreviation.not.unique")
        Assertions.assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
