package io.cloudflight.jems.server.project.service.partner.deactivate_project_partner

import io.cloudflight.jems.server.UnitTest
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
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class DeactivateProjectPartnerTest : UnitTest() {

    @MockK
    lateinit var persistence: PartnerPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var deactivateProjectPartner: DeactivateProjectPartner

    @TestFactory
    fun `should deactivate the partner when application is in a modifiable status after Approved`() =
        listOf(
            ApplicationStatus.MODIFICATION_PRECONTRACTING
        ).map { status ->
            DynamicTest.dynamicTest(
                "should deactivate the partner when application is in '$status' status"
            ) {
                every { persistence.deactivatePartner(PARTNER_ID) } just Runs
                every { persistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
                every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status)

                assertDoesNotThrow { deactivateProjectPartner.deactivate(PARTNER_ID) }
            }
        }

    @TestFactory
    fun `should throw PartnerCannotBeDeactivatedException when application is not in a modifiable status after Approved`() =
        listOf(
            *ApplicationStatus.values().filterNot { it == ApplicationStatus.MODIFICATION_PRECONTRACTING }.toTypedArray()
        ).map { status ->
            DynamicTest.dynamicTest(
                "should throw PartnerCannotBeDeactivatedException the partner when application is in '$status' status"
            ) {
                every { persistence.deactivatePartner(PARTNER_ID) } just Runs
                every { persistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
                every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status)

                assertThrows<PartnerCannotBeDeactivatedException> { deactivateProjectPartner.deactivate(PARTNER_ID) }
            }
        }
}
