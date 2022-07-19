package io.cloudflight.jems.server.project.service.contracting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.model.ManagementType
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

class ContractingValidatorTest : UnitTest() {

    companion object {
        private const val projectID = 12L
        private const val INPUT_ERROR = "common.error.input.invalid"
        private const val CONTRACTING_ERROR = "use.case.project.contracting.management.denied"
        private const val MONITORING_ERROR = "use.case.project.contracting.monitoring.denied"

        private val projectManagers = listOf(
            ProjectContractingManagement(
                projectId = projectID,
                managementType = ManagementType.ProjectManager,
                title = "Mr",
                firstName = "Test",
                lastName = "UserOne",
                email = "testuser1@jems.eu",
                telephone = "9212347801"
            ),
            ProjectContractingManagement(
                projectId = projectID,
                managementType = ManagementType.CommunicationManager,
                title = "Mr",
                firstName = "Test",
                lastName = "UserTwo",
                email = "testuser2@jems.eu",
                telephone = "8271929316"
            ),
            ProjectContractingManagement(
                projectId = projectID,
                managementType = ManagementType.FinanceManager,
                title = "Mrs",
                firstName = "Test",
                lastName = "UserThree",
                email = "testuser2@jems.eu",
                telephone = "56121347893"
            )
        )

        private val projectSummary = ProjectSummary(
            id = projectID,
            customIdentifier = "TSTCM",
            callName = "Test contracting management",
            acronym = "TCM",
            status = ApplicationStatus.APPROVED,
            firstSubmissionDate = ZonedDateTime.parse("2022-06-20T10:00:00+02:00"),
            lastResubmissionDate = ZonedDateTime.parse("2022-07-20T10:00:00+02:00"),
            specificObjectiveCode = "SO1.1",
            programmePriorityCode = "P1"
        )
    }

    lateinit var generalValidator: GeneralValidatorService

    lateinit var validator: ContractingValidator

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        generalValidator = GeneralValidatorDefaultImpl()
        validator = ContractingValidator(generalValidator)
    }

    @Test
    fun `should succeed on correct data`() {
        assertDoesNotThrow {
            validator.validateManagerContacts(projectManagers)
        }
    }

    @Test
    fun `should throw AppInputValidationException if title too long`() {
        val projectManagers = listOf(ProjectContractingManagement(
            projectId = projectID,
            managementType = ManagementType.ProjectManager,
            title = "title with text more than 25 characters long"
        ))

        val ex = assertThrows<AppInputValidationException> {
            validator.validateManagerContacts(projectManagers)
        }
        assertEquals(INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should throw AppInputValidationException if first name too long`() {
        val projectManagers = listOf(ProjectContractingManagement(
            projectId = projectID,
            managementType = ManagementType.ProjectManager,
            title = "",
            firstName = "first name with a text more than 50 characters long"
        ))

        val ex = assertThrows<AppInputValidationException> {
            validator.validateManagerContacts(projectManagers)
        }
        assertEquals(INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should throw AppInputValidationException if last name too long`() {
        val projectManagers = listOf(ProjectContractingManagement(
            projectId = projectID,
            managementType = ManagementType.FinanceManager,
            title = "",
            lastName = "last name with a text of more than 50 characters long"
        ))

        val ex = assertThrows<AppInputValidationException> {
            validator.validateManagerContacts(projectManagers)
        }
        assertEquals(INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should throw AppInputValidationException if email has wrong format`() {
        val projectManagers = listOf(ProjectContractingManagement(
            projectId = projectID,
            managementType = ManagementType.FinanceManager,
            title = "",
            email = "testATtest"
        ))

        val ex = assertThrows<AppInputValidationException> {
            validator.validateManagerContacts(projectManagers)
        }
        assertEquals(INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should throw AppInputValidationException if telephone has wrong format`() {
        val projectManagers = listOf(ProjectContractingManagement(
            projectId = projectID,
            managementType = ManagementType.FinanceManager,
            title = "",
            telephone = "telephone more than 25 chars"
        ))

        val ex = assertThrows<AppInputValidationException> {
            validator.validateManagerContacts(projectManagers)
        }
        assertEquals(INPUT_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should be successful if project status is correct`() {
        assertDoesNotThrow {
            validator.validateProjectStepAndStatus(projectSummary)
        }
    }

    @Test
    fun `should throw ContractingDeniedException if project status is DRAFT`() {
        val projectSummary = ProjectSummary(
            id = projectID,
            customIdentifier = "TST",
            callName = "Test contracting management",
            acronym = "TCM",
            status = ApplicationStatus.DRAFT
        )

        val ex = assertThrows<ContractingDeniedException> {
            validator.validateProjectStepAndStatus(projectSummary)
        }
        assertEquals(CONTRACTING_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should throw ContractingDeniedException if project status is in STEP 1`() {
        val projectSummary = ProjectSummary(
            id = projectID,
            customIdentifier = "TST",
            callName = "Test contracting management",
            acronym = "TCM",
            status = ApplicationStatus.STEP1_SUBMITTED
        )

        val ex = assertThrows<ContractingDeniedException> {
            validator.validateProjectStepAndStatus(projectSummary)
        }
        assertEquals(CONTRACTING_ERROR, ex.i18nMessage.i18nKey)
    }

    @Test
    fun `should succeed on project status approved`() {
        val projectSummary = ProjectSummary(
            id = projectID,
            customIdentifier = "TST",
            callName = "Test contracting monitoring",
            acronym = "TCM",
            status = ApplicationStatus.APPROVED
        )

        assertDoesNotThrow {
            validator.validateProjectStatusForModification(projectSummary)
        }
    }

    @Test
    fun `should throw ContractingDeniedException if project status is not approved yet`() {
        val projectSummary = ProjectSummary(
            id = projectID,
            customIdentifier = "TST",
            callName = "Test contracting monitoring",
            acronym = "TCM",
            status = ApplicationStatus.STEP1_DRAFT
        )

        val ex = assertThrows<ContractingModificationDeniedException> {
            validator.validateProjectStatusForModification(projectSummary)
        }
        assertEquals(MONITORING_ERROR, ex.i18nMessage.i18nKey)
    }
}
