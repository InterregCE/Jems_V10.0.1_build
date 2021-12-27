package io.cloudflight.jems.server.project.service.associatedorganization.get_associated_organization

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganization
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.associatedorganization.AssociatedOrganizationPersistence
import io.cloudflight.jems.server.project.repository.partner.associated_organization.toOutputProjectAssociatedOrganization
import io.cloudflight.jems.server.project.repository.partner.associated_organization.toOutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class GetAssociatedOrganizationInteractorTest : UnitTest() {

    @MockK
    lateinit var persistence: AssociatedOrganizationPersistence

    @InjectMockKs
    lateinit var getInteractor: GetAssociatedOrganization

    private val UNPAGED = Pageable.unpaged()

    private val userRole = UserRoleEntity(1, "ADMIN")
    private val user = UserEntity(
        id = 1,
        name = "Name",
        password = "hash",
        email = "admin@admin.dev",
        surname = "Surname",
        userRole = userRole,
        userStatus = UserStatus.ACTIVE
    )

    private val call = CallEntity(
        id = 1,
        creator = user,
        name = "call",
        status = CallStatus.DRAFT,
        startDate = ZonedDateTime.now(),
        endDateStep1 = null,
        endDate = ZonedDateTime.now(),
        prioritySpecificObjectives = mutableSetOf(),
        strategies = mutableSetOf(),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        lengthOfPeriod = 1
    )
    private val projectStatus = ProjectStatusHistoryEntity(
        status = ApplicationStatus.APPROVED,
        user = user,
        updated = ZonedDateTime.now()
    )
    private val project = ProjectEntity(
        id = 1,
        acronym = "acronym",
        call = call,
        applicant = user,
        currentStatus = projectStatus,
    )

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        legalStatus = ProgrammeLegalStatusEntity(id = 1),
        sortNumber = 1,
    )

    private val projectPartnerDTO = ProjectPartnerSummaryDTO(
        id = 1,
        active = true,
        abbreviation = projectPartner.abbreviation,
        role = ProjectPartnerRoleDTO.LEAD_PARTNER,
        sortNumber = 1,
    )

    private fun organization(id: Long, partner: ProjectPartnerEntity, name: String, sortNr: Int? = null) =
        ProjectAssociatedOrganization(
            id = id,
            project = partner.project,
            partner = partner,
            nameInOriginalLanguage = name,
            nameInEnglish = name,
            sortNumber = sortNr
        )

    private fun outputOrganization(id: Long, partnerAbbr: String, name: String, sortNr: Int? = null) =
        OutputProjectAssociatedOrganization(
            id = id,
            active = true,
            partnerAbbreviation = partnerAbbr,
            nameInOriginalLanguage = name,
            nameInEnglish = name,
            sortNumber = sortNr
        )

    private fun outputOrganizationDetail(
        id: Long,
        partner: ProjectPartnerSummaryDTO,
        name: String,
        sortNr: Int? = null
    ) =
        OutputProjectAssociatedOrganizationDetail(
            id = id,
            active = true,
            partner = partner,
            nameInOriginalLanguage = name,
            nameInEnglish = name,
            sortNumber = sortNr
        )

    @Test
    fun getById() {
        val org = organization(1, projectPartner, "test", 1)
        every { persistence.getById(1, 1) } returns org.toOutputProjectAssociatedOrganizationDetail()

        assertThat(getInteractor.getById(1, 1))
            .isEqualTo(outputOrganizationDetail(1, projectPartnerDTO, "test", 1))
    }

    @Test
    fun `getById not-existing`() {
        every { persistence.getById(1, -1) } throws ResourceNotFoundException("projectAssociatedOrganisation")
        assertThrows<ResourceNotFoundException> { getInteractor.getById(1, -1) }
    }

    @Test
    fun findAllByProjectId() {
        every { persistence.findAllByProjectId(1, UNPAGED) } returns
            PageImpl(listOf(organization(1, projectPartner, "test", 1).toOutputProjectAssociatedOrganization()))

        assertThat(getInteractor.findAllByProjectId(1, UNPAGED))
            .containsExactly(outputOrganization(1, projectPartner.abbreviation, "test", 1))
    }
}
