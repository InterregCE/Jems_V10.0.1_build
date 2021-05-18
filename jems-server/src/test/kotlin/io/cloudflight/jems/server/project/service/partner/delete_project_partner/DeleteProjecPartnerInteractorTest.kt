package io.cloudflight.jems.server.project.service.partner.delete_project_partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class DeleteProjecPartnerInteractorTest {
    @MockK
    lateinit var persistence: PartnerPersistence

    lateinit var deleteInteractor: DeleteProjectPartnerInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        deleteInteractor = DeleteProjectPartner(persistence)
    }

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

    @Test
    fun deleteProjectPartnerWithOrganization() {
        val projectPartnerWithOrganization = ProjectPartnerEntity(
            id = 1,
            project = ProjectPartnerTestUtil.project,
            abbreviation = "partner",
            role = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = projectPartnerWithOrganization.nameInOriginalLanguage,
            nameInEnglish = projectPartnerWithOrganization.nameInEnglish,
            translatedValues = projectPartnerWithOrganization.translatedValues,
            legalStatus = ProgrammeLegalStatusEntity(id = 1)
        )
        every { persistence.deletePartner(projectPartnerWithOrganization.id) } just Runs

        assertDoesNotThrow { deleteInteractor.deletePartner(projectPartnerWithOrganization.id) }
    }

    @Test
    fun deleteProjectPartnerWithoutOrganization() {
        every { persistence.deletePartner(projectPartner.id) } just Runs
        assertDoesNotThrow { deleteInteractor.deletePartner(projectPartner.id) }
    }

    @Test
    fun deleteProjectPartner_notExisting() {
        every { persistence.deletePartner(-1) } throws ResourceNotFoundException("partner")
        assertThrows<ResourceNotFoundException> { deleteInteractor.deletePartner(-1) }
    }
}