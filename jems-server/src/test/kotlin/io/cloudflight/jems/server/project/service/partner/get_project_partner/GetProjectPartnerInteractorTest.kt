package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ApplicationVersionNotFoundException
import io.cloudflight.jems.server.project.repository.partner.toModel
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerDetail
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
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

    private val projectPartnerEntity = ProjectPartnerEntity(
        id = 1,
        project = ProjectPartnerTestUtil.project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
        nace = NaceGroupLevel.A,
        otherIdentifierNumber = "id-12",
        pic = "009",
        legalStatus = ProgrammeLegalStatusEntity(id = 1),
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes
    )

    private val projectPartner = projectPartnerEntity.toModel()
    private val projectPartnerDetail = projectPartnerEntity.toProjectPartnerDetail()

    @Test
    fun getById() {
        every { persistence.getById(-1) } throws ResourceNotFoundException("partner")
        every { persistence.getById(1) } returns projectPartnerDetail

        assertThrows<ResourceNotFoundException> { getInteractor.getById(-1) }
        Assertions.assertThat(getInteractor.getById(1)).isEqualTo(projectPartnerDetail)
    }

    @Test
    fun getByIdAndVersion() {
        every { persistence.getById(1, "404") } throws ApplicationVersionNotFoundException()
        every { persistence.getById(1, "1.0") } returns projectPartnerDetail

        assertThrows<ApplicationVersionNotFoundException> { getInteractor.getById(1, "404") }
        Assertions.assertThat(getInteractor.getById(1, "1.0")).isEqualTo(projectPartnerDetail)
    }

    @Test
    fun findAllByProjectId() {
        every { persistence.findAllByProjectId(0, UNPAGED) } returns PageImpl(emptyList())
        every { persistence.findAllByProjectId(1, UNPAGED) } returns PageImpl(mutableListOf(projectPartner))

        Assertions.assertThat(getInteractor.findAllByProjectId(0, UNPAGED)).isEmpty()
        Assertions.assertThat(getInteractor.findAllByProjectId(1, UNPAGED)).containsExactly(projectPartner)
    }

    @Test
    fun findAllByProjectIdUnpaged() {
        every { persistence.findAllByProjectId(0) } returns PageImpl(emptyList())
        every { persistence.findAllByProjectId(1) } returns PageImpl(mutableListOf(projectPartnerDetail))

        Assertions.assertThat(getInteractor.findAllByProjectId(0)).isEmpty()
        Assertions.assertThat(getInteractor.findAllByProjectId(1)).containsExactly(projectPartnerDetail)
    }
}
