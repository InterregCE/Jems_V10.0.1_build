// todo check
//package io.cloudflight.jems.server.project.service.partner.update_project_partner
//
//import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
//import io.cloudflight.jems.api.project.dto.ProjectContactDTO
//import io.cloudflight.jems.api.project.dto.InputTranslation
//import io.cloudflight.jems.api.project.dto.ProjectContactTypeDTO
//import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
//import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
//import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDTO
//import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
//import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO
//import io.cloudflight.jems.server.UnitTest
//import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
//import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
//import io.cloudflight.jems.server.common.validator.GeneralValidatorService
//import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
//import io.cloudflight.jems.server.project.entity.TranslationPartnerId
//import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
//import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
//import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
//import io.cloudflight.jems.server.project.repository.partner.toEntity
//import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerDetailDTO
//import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
//import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
//import io.mockk.every
//import io.mockk.impl.annotations.InjectMockKs
//import io.mockk.impl.annotations.MockK
//import java.util.Optional
//import org.assertj.core.api.Assertions
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import org.springframework.http.HttpStatus
//
//internal class UpdateProjectpartnerInteractorTest: UnitTest() {
//    @MockK
//    lateinit var persistence: PartnerPersistence
//
//    @MockK
//    lateinit var projectPartnerRepository: ProjectPartnerRepository
//
//    @InjectMockKs
//    lateinit var updateInteractor: UpdateProjectPartner
//
//    val generalValidator: GeneralValidatorService = GeneralValidatorDefaultImpl()
//
//    private val partnerTranslatedValues =
//        mutableSetOf(ProjectPartnerTranslEntity(TranslationPartnerId(1, SystemLanguage.EN), "test"))
//    private val laegalStatus = ProgrammeLegalStatusEntity(id = 1)
//    private val projectPartnerWithOrganization = ProjectPartnerEntity(
//        id = 1,
//        project = ProjectPartnerTestUtil.project,
//        abbreviation = "partner",
//        role = ProjectPartnerRoleDTO.LEAD_PARTNER,
//        nameInOriginalLanguage = "test",
//        nameInEnglish = "test",
//        translatedValues = partnerTranslatedValues,
//        partnerType = ProjectTargetGroupDTO.BusinessSupportOrganisation,
//        legalStatus = laegalStatus,
//        vat = "test vat",
//        vatRecovery = ProjectPartnerVatRecoveryDTO.Yes
//    )
//    private val legalStatus = ProgrammeLegalStatusEntity(id = 1)
//
//    @Test
//    fun updateProjectPartner() {
//        val projectPartnerUpdate =
//            ProjectPartnerRequest(1, "updated", ProjectPartnerRoleDTO.PARTNER, legalStatusId = 1)
//        val updatedProjectPartner = ProjectPartnerEntity(
//            1,
//            ProjectPartnerTestUtil.project,
//            projectPartnerUpdate.abbreviation!!,
//            projectPartnerUpdate.role!!,
//            legalStatus = legalStatus
//        )
//        every { persistence.update(projectPartnerUpdate) } returns updatedProjectPartner.toProjectPartnerDetailDTO()
//        every { projectPartnerRepository.findById(1) } returns Optional.of(updatedProjectPartner)
//        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRoleDTO.PARTNER) } returns Optional.of(updatedProjectPartner)
//        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "updated") } returns Optional.of(updatedProjectPartner)
//
//
//        Assertions.assertThat(updateInteractor.update(projectPartnerUpdate))
//            .isEqualTo(updatedProjectPartner.toProjectPartnerDetailDTO())
//    }
//
//    @Test
//    fun updatePartnerContact() {
//        val projectPartnerContactUpdate = ProjectContactDTO(
//            ProjectContactTypeDTO.ContactPerson,
//            "test",
//            "test",
//            "test",
//            "test@ems.eu",
//            "test"
//        )
//        val projectPartner = ProjectPartnerEntity(
//            1,
//            ProjectPartnerTestUtil.project,
//            "updated",
//            ProjectPartnerRoleDTO.PARTNER,
//            legalStatus = ProgrammeLegalStatusEntity(id = 1),
//            partnerType = ProjectTargetGroupDTO.EducationTrainingCentreAndSchool
//        )
//        val contactPersonsEntity = setOf(projectPartnerContactUpdate.toEntity(projectPartner))
//        val updatedProjectPartner = projectPartner.copy(contacts = contactPersonsEntity)
//
//        every { persistence.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)) } returns updatedProjectPartner.toProjectPartnerDetailDTO()
//
//        Assertions.assertThat(updateInteractor.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)))
//            .isEqualTo(updatedProjectPartner.toProjectPartnerDetailDTO())
//    }
//
//    @Test
//    fun updatePartnerContact_notExisting() {
//        val projectPartnerContactUpdate = ProjectContactDTO(
//            ProjectContactTypeDTO.LegalRepresentative,
//            "test",
//            "test",
//            "test",
//            "test@ems.eu",
//            "test"
//        )
//        val contactPersonsDto = setOf(projectPartnerContactUpdate)
//        every { persistence.updatePartnerContacts(-1, contactPersonsDto) } throws ResourceNotFoundException("projectPartner")
//        val exception = assertThrows<ResourceNotFoundException> {
//            updateInteractor.updatePartnerContacts(
//                -1,
//                contactPersonsDto
//            )
//        }
//        Assertions.assertThat(exception.entity).isEqualTo("projectPartner")
//    }
//
//    @Test
//    fun updatePartnerMotivation() {
//        val projectPartnerMotivationUpdate = ProjectPartnerMotivationDTO(
//            setOf(InputTranslation(SystemLanguage.EN, "test")),
//            setOf(InputTranslation(SystemLanguage.EN, "test")),
//            setOf(InputTranslation(SystemLanguage.EN, "test"))
//        )
//        val projectPartner = ProjectPartnerEntity(
//            1,
//            ProjectPartnerTestUtil.project,
//            "updated",
//            ProjectPartnerRoleDTO.PARTNER,
//            legalStatus = ProgrammeLegalStatusEntity(id = 1),
//            partnerType = ProjectTargetGroupDTO.EducationTrainingCentreAndSchool
//        )
//        val updatedProjectPartner =
//            projectPartner.copy(motivation = projectPartnerMotivationUpdate.toEntity(projectPartner.id))
//
//        every { updateInteractor.updatePartnerMotivation(1, projectPartnerMotivationUpdate) } returns updatedProjectPartner.toProjectPartnerDetailDTO()
//
//        Assertions.assertThat(updateInteractor.updatePartnerMotivation(1, projectPartnerMotivationUpdate))
//            .isEqualTo(updatedProjectPartner.toProjectPartnerDetailDTO())
//    }
//
//    @Test
//    fun updatePartnerContribution_notExisting() {
//        val projectPartnerContributionUpdate = ProjectPartnerMotivationDTO(
//            setOf(InputTranslation(SystemLanguage.EN, "test")),
//            setOf(InputTranslation(SystemLanguage.EN, "test")),
//            setOf(InputTranslation(SystemLanguage.EN, "test"))
//        )
//        every { persistence.updatePartnerMotivation(-1, projectPartnerContributionUpdate) } throws ResourceNotFoundException("projectPartner")
//        val exception = assertThrows<ResourceNotFoundException> {
//            updateInteractor.updatePartnerMotivation(
//                -1,
//                projectPartnerContributionUpdate
//            )
//        }
//        Assertions.assertThat(exception.entity).isEqualTo("projectPartner")
//    }
//
//    @Test
//    fun updateProjectPartnerWithOrganization() {
//        val projectPartnerUpdate = ProjectPartnerRequest(
//            1, "updated", ProjectPartnerRoleDTO.PARTNER, null, "test", "test", setOf(
//                InputTranslation(
//                    SystemLanguage.EN, "test"
//                )
//            ), legalStatusId = 1
//        )
//        val updatedProjectPartner = ProjectPartnerEntity(
//            id = 1,
//            project = ProjectPartnerTestUtil.project,
//            abbreviation = projectPartnerUpdate.abbreviation!!,
//            role = projectPartnerUpdate.role!!,
//            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
//            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
//            translatedValues = projectPartnerWithOrganization.translatedValues,
//            legalStatus = legalStatus
//        )
//        every { updateInteractor.update(projectPartnerUpdate) } returns updatedProjectPartner.toProjectPartnerDetailDTO()
//        every { projectPartnerRepository.findById(1) } returns Optional.of(updatedProjectPartner)
//
//        Assertions.assertThat(updateInteractor.update(projectPartnerUpdate))
//            .isEqualTo(updatedProjectPartner.toProjectPartnerDetailDTO())
//    }
//
//    @Test
//    fun `error on already existing partner name when updating`() {
//        val projectPartnerRequest = ProjectPartnerDTO("partner", ProjectPartnerRoleDTO.LEAD_PARTNER, legalStatusId = 1)
//        val updateProjectPartner = ProjectPartnerRequest(1, "partner", ProjectPartnerRoleDTO.PARTNER, legalStatusId = 1)
//        val projectPartnerWithProject = ProjectPartnerEntity(0,
//            ProjectPartnerTestUtil.project, projectPartnerRequest.abbreviation!!, projectPartnerRequest.role!!, legalStatus = legalStatus)
//        val oldProjectPartnerWithProject = ProjectPartnerEntity(0,
//            ProjectPartnerTestUtil.project, "old partner", projectPartnerRequest.role!!, legalStatus = legalStatus)
//
//        every { projectPartnerRepository.findById(1) } returns Optional.of(oldProjectPartnerWithProject)
//        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.of(projectPartnerWithProject)
//        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 1
//
//        val ex = assertThrows<PartnerAbbreviationNotUnique> {
//            updateInteractor.update(updateProjectPartner)
//        }
//
//        Assertions.assertThat(ex.i18nMessage.i18nKey).isEqualTo("use.case.update.project.partner.abbreviation.not.unique")
//        Assertions.assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
//    }
//}
