package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.utils.partner.PROJECT_ID
import io.cloudflight.jems.server.utils.partner.projectPartner
import io.cloudflight.jems.server.utils.partner.projectPartnerDetail
import io.cloudflight.jems.server.utils.partner.projectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

internal class CreateProjectPartnerInteractorTest : UnitTest() {
    @MockK
    lateinit var persistence: PartnerPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var createProjectPartner: CreateProjectPartner

    companion object {
        private val projectPartner = projectPartner(id = 0)
        private val projectPartnerDetail = projectPartnerDetail()
        private val projectCallSettings =
            ProjectCallSettings(
                callId = 2L,
                callName = "call",
                callType = CallType.STANDARD,
                startDate = ZonedDateTime.now().minusDays(2),
                endDate = ZonedDateTime.now().plusDays(2),
                lengthOfPeriod = 2,
                endDateStep1 = ZonedDateTime.now().plusDays(2),
                flatRates = emptySet(),
                lumpSums = emptyList(),
                unitCosts = emptyList(),
                stateAids = emptyList(),
                isAdditionalFundAllowed = false,
                applicationFormFieldConfigurations = mutableSetOf(),
                preSubmissionCheckPluginKey = null,
                firstStepPreSubmissionCheckPluginKey = null
            )
    }

    @BeforeEach
    fun reset() {
        clearMocks(generalValidator, persistence)
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns projectCallSettings
    }

    @Test
    fun `should validate input when creating the partner`() {
        every { persistence.create(PROJECT_ID, projectPartner, true) } returns projectPartnerDetail
        every { persistence.countByProjectId(PROJECT_ID) } returns 0
        every { persistence.changeRoleOfLeadPartnerToPartnerIfItExists(PROJECT_ID) } returns Unit
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary()
        every {
            persistence.throwIfPartnerAbbreviationAlreadyExists(PROJECT_ID, projectPartner.abbreviation!!)
        } returns Unit

        createProjectPartner.create(PROJECT_ID, projectPartner)

        verify(exactly = 1) { generalValidator.nullOrZero(projectPartner.id, "id") }
        verify(exactly = 1) { generalValidator.notNull(projectPartner.role, "role") }
        verify(exactly = 1) { generalValidator.notBlank(projectPartner.abbreviation, "abbreviation") }
        verify(exactly = 1) { generalValidator.maxLength(projectPartner.abbreviation, 15, "abbreviation") }
        verify(exactly = 1) {
            generalValidator.maxLength(projectPartner.nameInOriginalLanguage, 100, "nameInOriginalLanguage")
        }
        verify(exactly = 1) { generalValidator.maxLength(projectPartner.nameInEnglish, 100, "nameInEnglish") }
        verify(exactly = 1) { generalValidator.notNull(projectPartner.legalStatusId, "legalStatusId") }
        verify(exactly = 1) {
            generalValidator.maxLength(projectPartner.otherIdentifierNumber, 50, "otherIdentifierNumber")
        }
        verify(exactly = 1) {
            generalValidator.maxLength(projectPartner.otherIdentifierDescription, 100, "otherIdentifierDescription")
        }
        verify(exactly = 1) { generalValidator.exactLength(projectPartner.pic, 9, "pic") }
        verify(exactly = 1) { generalValidator.onlyDigits(projectPartner.pic, "pic") }
        verify(exactly = 1) { generalValidator.maxLength(projectPartner.vat, 50, "vat") }
    }

    @Test
    fun `creating the partner fails if existing active for SPF`() {
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns projectCallSettings.copy(callType = CallType.SPF)
        every { persistence.countByProjectIdActive(PROJECT_ID) } returns 1

        assertThrows<MaximumNumberOfActivePartnersReached> { createProjectPartner.create(PROJECT_ID, projectPartner) }
    }

    @Test
    fun `creating the partner successful if no existing active for SPF`() {
        every { projectPersistence.getProjectCallSettings(PROJECT_ID) } returns projectCallSettings.copy(callType = CallType.SPF)
        every { persistence.countByProjectIdActive(PROJECT_ID) } returns 0

        every { persistence.countByProjectId(PROJECT_ID) } returns 0
        every { persistence.changeRoleOfLeadPartnerToPartnerIfItExists(PROJECT_ID) } returns Unit
        every { persistence.create(PROJECT_ID, projectPartner, true) } returns projectPartnerDetail
        every {
            persistence.throwIfPartnerAbbreviationAlreadyExists(PROJECT_ID, projectPartner.abbreviation!!)
        } returns Unit
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary()

        assertThat(createProjectPartner.create(PROJECT_ID, projectPartner)).isEqualTo(projectPartnerDetail)
    }

    @Test
    fun `should create a new partner for the project when there is no problem`() {
        val projectPartner = projectPartner(role = ProjectPartnerRole.PARTNER)
        every { persistence.create(PROJECT_ID, projectPartner, true) } returns projectPartnerDetail
        every { persistence.countByProjectId(PROJECT_ID) } returns 0
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary()
            every {
                persistence.throwIfPartnerAbbreviationAlreadyExists(PROJECT_ID, projectPartner.abbreviation!!)
            } returns Unit

        assertThat(createProjectPartner.create(PROJECT_ID, projectPartner)).isEqualTo(projectPartnerDetail)

        verify(exactly = 1) {
            persistence.throwIfPartnerAbbreviationAlreadyExists(PROJECT_ID, projectPartner.abbreviation!!)
        }
    }

    @Test
    fun `should change role of current lead partner (if exists) to partner when creating a new lead partner`() {

        every { persistence.countByProjectId(PROJECT_ID) } returns 1
        every { persistence.changeRoleOfLeadPartnerToPartnerIfItExists(PROJECT_ID) } returns Unit
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary()
        every {
            persistence.throwIfPartnerAbbreviationAlreadyExists(
                PROJECT_ID,
                projectPartner.abbreviation!!
            )
        } returns Unit
        every { persistence.create(PROJECT_ID, projectPartner, true) } returns projectPartnerDetail

        createProjectPartner.create(PROJECT_ID, projectPartner)

        verify(exactly = 1) { persistence.changeRoleOfLeadPartnerToPartnerIfItExists(PROJECT_ID) }
    }

    @Test
    fun `should throw MaximumNumberOfPartnersReached when number of partners already reached the max allowed number of partners for a project`() {
        every { persistence.countByProjectId(PROJECT_ID) } returns 30

        val ex = assertThrows<MaximumNumberOfPartnersReached> {
            createProjectPartner.create(PROJECT_ID, projectPartner)
        }
        assertThat(ex.i18nMessage.i18nKey).isEqualTo("use.case.create.project.partner.max.allowed.count.reached")
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @TestFactory
    fun `should not resort partners when project is not in a modifiable status before Approved`() =
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
                "should not resort partners when project is in '$status' status"
            ) {
                val projectPartner = projectPartner(role = ProjectPartnerRole.PARTNER)
                val shouldResortPartnersSlot = slot<Boolean>()
                every { persistence.create(PROJECT_ID,projectPartner, capture(shouldResortPartnersSlot)) } returns projectPartnerDetail
                every { persistence.countByProjectId(PROJECT_ID) } returns 0
                every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status)
                every {
                    persistence.throwIfPartnerAbbreviationAlreadyExists(PROJECT_ID, projectPartner.abbreviation!!)
                } returns Unit

                createProjectPartner.create(PROJECT_ID, projectPartner)

                assertThat(shouldResortPartnersSlot.captured).isFalse()
            }
        }


    @TestFactory
    fun `should resort partners when project is in a modifiable status before Approved`() =
        listOf(
            ApplicationStatus.STEP1_DRAFT,
            ApplicationStatus.DRAFT,
            ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS,
            ApplicationStatus.RETURNED_TO_APPLICANT,
        ).map { status ->
            DynamicTest.dynamicTest(
                "should resort partners when project is in '$status' status"
            ) {
                val projectPartner = projectPartner(role = ProjectPartnerRole.PARTNER)
                val shouldResortPartnersSlot = slot<Boolean>()
                every { persistence.create(PROJECT_ID,projectPartner, capture(shouldResortPartnersSlot)) } returns projectPartnerDetail
                every { persistence.countByProjectId(PROJECT_ID) } returns 0
                every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status)
                every {
                    persistence.throwIfPartnerAbbreviationAlreadyExists(PROJECT_ID, projectPartner.abbreviation!!)
                } returns Unit

                createProjectPartner.create(PROJECT_ID, projectPartner)

                assertThat(shouldResortPartnersSlot.captured).isTrue()
            }
        }

}
