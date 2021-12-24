package io.cloudflight.jems.server.project.service.partner.delete_project_partner

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.utils.PARTNER_ID
import io.cloudflight.jems.server.utils.partner.PROJECT_ID
import io.cloudflight.jems.server.utils.partner.projectSummary
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class DeleteProjectPartnerInteractorTest : UnitTest() {
    @MockK
    lateinit var persistence: PartnerPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var deleteInteractor: DeleteProjectPartner

    @Test
    fun deleteProjectPartnerWithOrganization() {

        every { persistence.deletePartner(PARTNER_ID) } just Runs
        every { persistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary()

        assertDoesNotThrow { deleteInteractor.deletePartner(PARTNER_ID) }
    }

    @Test
    fun deleteProjectPartner_notExisting() {
        every { persistence.deletePartner(PARTNER_ID) } throws ResourceNotFoundException("partner")
        every { persistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary()
        assertThrows<ResourceNotFoundException> { deleteInteractor.deletePartner(PARTNER_ID) }
    }

    @TestFactory
    fun `should delete partner when project is in a modifiable status before Approved`() =
        listOf(
            ApplicationStatus.STEP1_DRAFT,
            ApplicationStatus.DRAFT,
            ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS,
            ApplicationStatus.RETURNED_TO_APPLICANT,
        ).map { status ->
            DynamicTest.dynamicTest(
                "should throw PartnerCannotBeDeletedException when project is in '$status' status"
            ) {
                every { persistence.deletePartner(PARTNER_ID) } just Runs
                every { persistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
                every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status = status)
                assertDoesNotThrow { deleteInteractor.deletePartner(PARTNER_ID) }
            }
        }


    @TestFactory
    fun `should throw PartnerCannotBeDeletedException when project is not in a modifiable status before Approved`() =
        listOf(
            *ApplicationStatus.values().filterNot {
                listOf(
                    ApplicationStatus.STEP1_DRAFT,
                    ApplicationStatus.DRAFT,
                    ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS,
                    ApplicationStatus.RETURNED_TO_APPLICANT
                ).contains(it)
            }.toTypedArray()
        ).map { status ->
            DynamicTest.dynamicTest(
                "should throw PartnerCannotBeDeletedException when project is in '$status' status"
            ) {
                every { persistence.deletePartner(PARTNER_ID) } just Runs
                every { persistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
                every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status = status)
                assertThrows<PartnerCannotBeDeletedException> { deleteInteractor.deletePartner(PARTNER_ID) }
            }
        }

}
