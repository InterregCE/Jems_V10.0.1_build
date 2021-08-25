package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.model.ProjectContactType
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.utils.partner.PARTNER_ID
import io.cloudflight.jems.server.utils.partner.PROJECT_ID
import io.cloudflight.jems.server.utils.partner.projectPartner
import io.cloudflight.jems.server.utils.partner.projectPartnerDetail
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateProjectPartnerInteractorTest : UnitTest() {
    @MockK
    lateinit var persistence: PartnerPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updateProjectPartner: UpdateProjectPartner

    private val projectPartner = projectPartner(id = PARTNER_ID)
    private val projectPartnerDetail = projectPartnerDetail(id = PARTNER_ID)

    @BeforeEach
    fun reset() {
        clearMocks(generalValidator, persistence)
    }

    @Nested
    inner class Update {
        @Test
        fun `should validate input when creating the partner`() {
            every { persistence.getById(PARTNER_ID) } returns projectPartnerDetail
            every { persistence.update(projectPartner) } returns projectPartnerDetail

            updateProjectPartner.update(projectPartner)

            verify(exactly = 1) { generalValidator.notNull(projectPartner.id, "id") }
            verify(exactly = 1) { generalValidator.notNull(projectPartner.role, "role") }
            verify(exactly = 1) { generalValidator.notBlank(projectPartner.abbreviation, "abbreviation") }
            verify(exactly = 1) { generalValidator.maxLength(projectPartner.abbreviation, 15, "abbreviation") }
            verify(exactly = 1) {
                generalValidator.maxLength(projectPartner.nameInOriginalLanguage, 100, "nameInOriginalLanguage")
            }
            verify(exactly = 1) {
                generalValidator.maxLength(projectPartner.nameInEnglish, 100, "nameInEnglish")
            }
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
        fun `should update partner when there is no problem`() {
            val projectPartnerUpdate = projectPartner(PARTNER_ID, role = ProjectPartnerRole.PARTNER)
            val olProjectPartner = projectPartnerDetail(PARTNER_ID)
            val updatedProjectPartner = projectPartnerDetail(PARTNER_ID, role = ProjectPartnerRole.PARTNER)
            every { persistence.getById(PARTNER_ID) } returns olProjectPartner
            every { persistence.update(projectPartnerUpdate) } returns updatedProjectPartner

            assertThat(updateProjectPartner.update(projectPartnerUpdate))
                .isEqualTo(updatedProjectPartner)
        }

        @Test
        fun `should change role of current lead partner (if exists) to partner when updating role of partner to lead partner`() {
            val projectPartnerUpdate = projectPartner(PARTNER_ID)
            val olProjectPartner = projectPartnerDetail(PARTNER_ID, role = ProjectPartnerRole.PARTNER)
            val updatedProjectPartner = projectPartnerDetail(PARTNER_ID)
            every { persistence.getById(PARTNER_ID) } returns olProjectPartner
            every { persistence.changeRoleOfLeadPartnerToPartnerIfItExists(PROJECT_ID) } returns Unit
            every { persistence.update(projectPartnerUpdate) } returns updatedProjectPartner

            updateProjectPartner.update(projectPartnerUpdate)

            verify(exactly = 1) { persistence.changeRoleOfLeadPartnerToPartnerIfItExists(PROJECT_ID) }
        }

        @Test
        fun `should check if partner abbreviation already exists when updating abbreviation of partner`() {
            val projectPartnerUpdate = projectPartner(PARTNER_ID, abbreviation = "new")
            val olProjectPartner = projectPartnerDetail(PARTNER_ID, abbreviation = "old")
            val updatedProjectPartner = projectPartnerDetail(PARTNER_ID)
            every { persistence.getById(PARTNER_ID) } returns olProjectPartner
            every {
                persistence.throwIfPartnerAbbreviationAlreadyExists(PROJECT_ID, projectPartnerUpdate.abbreviation!!)
            } returns Unit
            every { persistence.update(projectPartnerUpdate) } returns updatedProjectPartner

            updateProjectPartner.update(projectPartnerUpdate)

            verify(exactly = 1) {
                persistence.throwIfPartnerAbbreviationAlreadyExists(
                    PROJECT_ID, projectPartnerUpdate.abbreviation!!
                )
            }
        }
    }

    @Test
    fun updatePartnerContact() {
        val projectPartnerContactUpdate = ProjectPartnerContact(
            ProjectContactType.ContactPerson, "test", "test", "test", "test@ems.eu", "test"
        )

        val updatedProjectPartner = projectPartnerDetail(1, contacts = listOf(projectPartnerContactUpdate))

        every { persistence.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)) } returns updatedProjectPartner

        assertThat(updateProjectPartner.updatePartnerContacts(1, setOf(projectPartnerContactUpdate)))
            .isEqualTo(updatedProjectPartner)
    }

    @Test
    fun updatePartnerContact_notExisting() {
        val projectPartnerContactUpdate = ProjectPartnerContact(
            ProjectContactType.ContactPerson, "test", "test", "test", "test@ems.eu", "test"
        )
        val contactPersons = setOf(projectPartnerContactUpdate)
        every {
            persistence.updatePartnerContacts(-1, contactPersons)
        } throws ResourceNotFoundException("projectPartner")
        val exception = assertThrows<ResourceNotFoundException> {
            updateProjectPartner.updatePartnerContacts(-1, contactPersons)
        }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

    @Test
    fun updatePartnerMotivation() {
        val projectPartnerMotivationUpdate = ProjectPartnerMotivation(
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test"))
        )

        val updatedProjectPartner = projectPartnerDetail(1, motivation = projectPartnerMotivationUpdate)

        every {
            updateProjectPartner.updatePartnerMotivation(1, projectPartnerMotivationUpdate)
        } returns updatedProjectPartner

        assertThat(updateProjectPartner.updatePartnerMotivation(1, projectPartnerMotivationUpdate))
            .isEqualTo(updatedProjectPartner)
    }

    @Test
    fun updatePartnerContribution_notExisting() {
        val projectPartnerContributionUpdate = ProjectPartnerMotivation(
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test")),
            setOf(InputTranslation(SystemLanguage.EN, "test"))
        )
        every {
            persistence.updatePartnerMotivation(-1, projectPartnerContributionUpdate)
        } throws ResourceNotFoundException("projectPartner")
        val exception = assertThrows<ResourceNotFoundException> {
            updateProjectPartner.updatePartnerMotivation(-1, projectPartnerContributionUpdate)
        }
        assertThat(exception.entity).isEqualTo("projectPartner")
    }

}
