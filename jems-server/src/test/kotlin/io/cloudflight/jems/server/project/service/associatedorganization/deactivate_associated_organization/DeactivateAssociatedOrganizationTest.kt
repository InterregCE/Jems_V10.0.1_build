package io.cloudflight.jems.server.project.service.associatedorganization.deactivate_associated_organization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.associatedorganization.AssociatedOrganizationPersistence
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

internal class DeactivateAssociatedOrganizationTest : UnitTest() {
    @MockK
    lateinit var persistence: AssociatedOrganizationPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var deactivateAssociatedOrganization: DeactivateAssociatedOrganization

    val associatedOrganizationId = 1L

    @TestFactory
    fun `should deactivate the associated organization when application is in a modifiable status after Approved`() =
        listOf(
            ApplicationStatus.MODIFICATION_PRECONTRACTING
        ).map { status ->
            DynamicTest.dynamicTest(
                "should deactivate the associated organization when application is in '$status' status"
            ) {
                every { persistence.deactivate(associatedOrganizationId) } just Runs
                every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status)
                assertDoesNotThrow { deactivateAssociatedOrganization.deactivate(PROJECT_ID, associatedOrganizationId) }
            }
        }

    @TestFactory
    fun `should throw AssociatedOrganizationCannotBeDeactivatedException when application is not in a modifiable status after Approved`() =
        listOf(
            *ApplicationStatus.values().filterNot { it == ApplicationStatus.MODIFICATION_PRECONTRACTING || it == ApplicationStatus.IN_MODIFICATION }.toTypedArray()
        ).map { status ->
            DynamicTest.dynamicTest(
                "should throw AssociatedOrganizationCannotBeDeactivatedException when application is in '$status' status"
            ) {
                every { persistence.deactivate(associatedOrganizationId) } just Runs
                every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status)

                assertThrows<AssociatedOrganizationCannotBeDeactivatedException> {
                    deactivateAssociatedOrganization.deactivate(
                        PROJECT_ID, associatedOrganizationId
                    )
                }
            }
        }
}
