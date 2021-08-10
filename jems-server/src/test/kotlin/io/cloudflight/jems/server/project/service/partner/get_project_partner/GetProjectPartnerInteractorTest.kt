package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ApplicationVersionNotFoundException
import io.cloudflight.jems.server.project.repository.partner.toDto
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerDetailDTO
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class GetProjectPartnerInteractorTest: UnitTest() {
    @MockK
    lateinit var persistence: PartnerPersistence

    @InjectMockKs
    lateinit var getInteractor: GetProjectPartner

    private val UNPAGED = Pageable.unpaged()

    private val projectPartner = ProjectPartnerEntity(
        id = 1,
        project = ProjectPartnerTestUtil.project,
        abbreviation = "partner",
        role = ProjectPartnerRoleDTO.LEAD_PARTNER,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        legalStatus = ProgrammeLegalStatusEntity(id = 1),
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecoveryDTO.Yes
    )

    private val projectPartnerDTO = projectPartner.toDto()
    private val projectPartnerDetailDTO = projectPartner.toProjectPartnerDetailDTO()

    @Test
    fun getById() {
        every { persistence.getById(-1) } throws ResourceNotFoundException("partner")
        every { persistence.getById(1) } returns projectPartnerDetailDTO

        assertThrows<ResourceNotFoundException> { getInteractor.getById(-1) }
        Assertions.assertThat(getInteractor.getById(1)).isEqualTo(projectPartnerDetailDTO)
    }

    @Test
    fun getByIdAndVersion() {
        every { persistence.getById(1, "404") } throws ApplicationVersionNotFoundException()
        every { persistence.getById(1, "1.0") } returns projectPartnerDetailDTO

        assertThrows<ApplicationVersionNotFoundException> { getInteractor.getById(1, "404") }
        Assertions.assertThat(getInteractor.getById(1, "1.0")).isEqualTo(projectPartnerDetailDTO)
    }

    @Test
    fun findAllByProjectId() {
        every { persistence.findAllByProjectId(0, UNPAGED) } returns PageImpl(emptyList())
        every { persistence.findAllByProjectId(1, UNPAGED) } returns PageImpl(mutableListOf(projectPartnerDTO))

        Assertions.assertThat(getInteractor.findAllByProjectId(0, UNPAGED)).isEmpty()
        Assertions.assertThat(getInteractor.findAllByProjectId(1, UNPAGED)).containsExactly(projectPartnerDTO)
    }

    @Test
    fun findAllByProjectIdUnpaged() {
        every { persistence.findAllByProjectId(0) } returns PageImpl(emptyList())
        every { persistence.findAllByProjectId(1) } returns PageImpl(mutableListOf(projectPartnerDetailDTO))

        Assertions.assertThat(getInteractor.findAllByProjectId(0)).isEmpty()
        Assertions.assertThat(getInteractor.findAllByProjectId(1)).containsExactly(projectPartnerDetailDTO)
    }
}
