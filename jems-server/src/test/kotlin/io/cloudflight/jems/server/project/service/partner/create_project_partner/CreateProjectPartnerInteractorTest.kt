// todo check
//package io.cloudflight.jems.server.project.service.partner.create_project_partner
//
//import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
//import io.cloudflight.jems.api.project.dto.InputTranslation
//import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDTO
//import io.cloudflight.jems.server.UnitTest
//import io.cloudflight.jems.server.common.entity.TranslationId
//import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
//import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
//import io.cloudflight.jems.server.common.validator.GeneralValidatorService
//import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
//import io.cloudflight.jems.server.project.entity.AddressEntity
//import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressEntity
//import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
//import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
//import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
//import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
//import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerDetail
//import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
//import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
//import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
//import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
//import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
//import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
//import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
//import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
//import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
//import io.mockk.every
//import io.mockk.impl.annotations.InjectMockKs
//import io.mockk.impl.annotations.MockK
//import java.util.Optional
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import org.springframework.http.HttpStatus
//
//internal class CreateProjectPartnerInteractorTest: UnitTest() {
//    @MockK
//    lateinit var persistence: PartnerPersistence
//
//    @MockK
//    lateinit var projectPartnerRepository: ProjectPartnerRepository
//
//    @InjectMockKs
//    lateinit var createInteractor: CreateProjectPartner
//
//    val generalValidator: GeneralValidatorService = GeneralValidatorDefaultImpl()
//
//    private val projectPartnerEntity = ProjectPartnerEntity(
//        id = 1,
//        project = ProjectPartnerTestUtil.project,
//        abbreviation = "partner",
//        role = ProjectPartnerRole.LEAD_PARTNER,
//        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
//        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
//        nace = NaceGroupLevel.A,
//        otherIdentifierNumber = "id-12",
//        pic = "009",
//        legalStatus = ProgrammeLegalStatusEntity(id = 1,),
//        vat = "test vat",
//        vatRecovery = ProjectPartnerVatRecovery.Yes
//    )
//    private val partnerTranslatedValues =
//        mutableSetOf(ProjectPartnerTranslEntity(TranslationId(projectPartnerEntity, SystemLanguage.EN), "test"))
//    private val legalStatus = ProgrammeLegalStatusEntity(id = 1)
//    private val partnerAddressEntity = ProjectPartnerAddressEntity(
//        addressId = ProjectPartnerAddressId(1, ProjectPartnerAddressType.Organization),
//        address = AddressEntity("AT")
//    )
//    private val projectPartnerWithOrganization = ProjectPartnerEntity(
//        id = 1,
//        project = ProjectPartnerTestUtil.project,
//        abbreviation = "partner",
//        role = ProjectPartnerRole.LEAD_PARTNER,
//        nameInOriginalLanguage = "test",
//        nameInEnglish = "test",
//        translatedValues = partnerTranslatedValues,
//        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
//        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
//        nace = NaceGroupLevel.A,
//        otherIdentifierNumber = "id-12",
//        pic = "009",
//        legalStatus = legalStatus,
//        vat = "test vat",
//        vatRecovery = ProjectPartnerVatRecovery.Yes,
//        addresses = setOf(partnerAddressEntity)
//    )
//
//    private val projectPartnerInclTransl =
//        projectPartnerEntity.copy(translatedValues = partnerTranslatedValues)
//
//    private val projectPartnerDetail = projectPartnerEntity.toProjectPartnerDetail()
//
//    @Test
//    fun createProjectPartner() {
//        val projectPartnerRequest =
//            ProjectPartner(0L, "partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
//        every { persistence.create(1, projectPartnerRequest) } returns projectPartnerDetail
//        every { projectPartnerRepository.countByProjectId(1) } returns 0
//        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
//        every { projectPartnerRepository.existsByProjectIdAndAbbreviation(1, "partner") } returns false
//
//
//        assertThat(createInteractor.create(1, projectPartnerRequest)).isEqualTo(projectPartnerDetail)
//    }
//
//    @Test
//    fun `createProjectPartner not existing`() {
//        val projectPartnerRequest = ProjectPartner(null,
//            "partner", ProjectPartnerRole.LEAD_PARTNER,  "test", "test", setOf(
//                InputTranslation(
//                    SystemLanguage.EN, "test"
//                )
//            ), ProjectTargetGroup.BusinessSupportOrganisation,
//            1, "test vat", ProjectPartnerVatRecovery.Yes
//        )
//        every { persistence.create(-1, projectPartnerRequest) } throws ResourceNotFoundException("project")
//        every { projectPartnerRepository.countByProjectId(-1) } returns 0
//        every { projectPartnerRepository.findFirstByProjectIdAndRole(-1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.empty()
//        every { projectPartnerRepository.existsByProjectIdAndAbbreviation(-1, "partner") } returns false
//        val ex = assertThrows<ResourceNotFoundException> { createInteractor.create(-1, projectPartnerRequest) }
//        assertThat(ex.entity).isEqualTo("project")
//    }
//
//    @Test
//    fun createProjectPartnerWithOrganization() {
//        val projectPartnerRequest = ProjectPartnerDTO(
//            "partner", ProjectPartnerRole.LEAD_PARTNER, null, "test", "test", setOf(
//                InputTranslation(
//                    SystemLanguage.EN, "test"
//                )
//            ), legalStatusId = 1
//        )
//        every { persistence.create(1, projectPartnerRequest) } returns projectPartnerWithOrganization.toProjectPartnerDetail()
//        assertThat(
//            createInteractor.create(
//                1,
//                projectPartnerRequest
//            )
//        ).isEqualTo(
//            ProjectPartnerDetail(
//                id = projectPartnerWithOrganization.id,
//                abbreviation = projectPartnerWithOrganization.abbreviation,
//                role = projectPartnerWithOrganization.role,
//                sortNumber = projectPartnerWithOrganization.sortNumber,
//                nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
//                nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
//                department = setOf(InputTranslation(SystemLanguage.EN, "test")),
//                partnerType = projectPartnerWithOrganization.partnerType,
//                vat = projectPartnerWithOrganization.vat,
//                vatRecovery = projectPartnerWithOrganization.vatRecovery,
//                legalStatusId = projectPartnerWithOrganization.legalStatus.id,
//                addresses = listOf(ProjectPartnerAddress(
//                    type = partnerAddressEntity.addressId.type,
//                    country = partnerAddressEntity.address.country
//                ))
//            )
//        )
//    }
//
//    @Test
//    fun `error on already existing partner name when creating`() {
//        val projectPartnerRequest = ProjectPartnerDTO("partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
//        val inputProjectPartner2 = ProjectPartnerDTO("partner", ProjectPartnerRole.PARTNER, legalStatusId = 1)
//        val projectPartnerWithProject = ProjectPartnerEntity(0,
//            ProjectPartnerTestUtil.project, projectPartnerRequest.abbreviation!!, projectPartnerRequest.role!!, legalStatus = legalStatus)
//
//        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.of(projectPartnerWithProject)
//        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 0
//
//        val ex = assertThrows<PartnerAbbreviationNotUnique> {
//            createInteractor.create(1, inputProjectPartner2)
//        }
//
//        assertThat(ex.i18nMessage.i18nKey).isEqualTo("use.case.create.project.partner.abbreviation.not.unique")
//        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
//    }
//
//    @Test
//    fun `error on multiple LEAD_PARTNER partner creation attempt`() {
//        val inputProjectPartnerLead =
//            ProjectPartnerDTO("partnerLead", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
//
//        every { persistence.create(1, inputProjectPartnerLead) } returns projectPartnerDetail
//        every { projectPartnerRepository.countByProjectId(1) } returns 1
//        every { projectPartnerRepository.findFirstByProjectIdAndRole(1, ProjectPartnerRole.LEAD_PARTNER) } returns Optional.of(projectPartnerEntity)
//        every { projectPartnerRepository.findFirstByProjectIdAndAbbreviation(1, "partner") } returns Optional.empty()
//
//        assertThrows<LeadPartnerAlreadyExists> { createInteractor.create(1, inputProjectPartnerLead) }
//    }
//
//    @Test
//    fun `error createProjectPartner when MAX count exceeded`() {
//        every { projectPartnerRepository.countByProjectId(eq(1)) } returns 30
//
//        val ex = assertThrows<MaximumNumberOfPartnersReached> {
//            createInteractor.create(
//                1, ProjectPartner(
//                    0L, "partner", ProjectPartnerRole.PARTNER,  "test", "test", setOf(
//                        InputTranslation(
//                            SystemLanguage.EN, "test"
//                        )
//                    ), ProjectTargetGroup.BusinessSupportOrganisation,
//                    1, "test vat", ProjectPartnerVatRecovery.Yes
//                )
//            )
//        }
//        assertThat(ex.i18nMessage.i18nKey).isEqualTo("use.case.create.project.partner.max.allowed.count.reached")
//        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
//    }
//}
