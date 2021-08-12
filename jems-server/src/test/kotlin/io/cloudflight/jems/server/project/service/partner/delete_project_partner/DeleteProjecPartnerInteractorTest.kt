package io.cloudflight.jems.server.project.service.partner.delete_project_partner

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.utils.PARTNER_ID
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class DeleteProjectPartnerInteractorTest : UnitTest() {
    @MockK
    lateinit var persistence: PartnerPersistence

    @InjectMockKs
    lateinit var deleteInteractor: DeleteProjectPartner

    @Test
    fun deleteProjectPartnerWithOrganization() {

        every { persistence.deletePartner(PARTNER_ID) } just Runs

        assertDoesNotThrow { deleteInteractor.deletePartner(PARTNER_ID) }
    }

    @Test
    fun deleteProjectPartner_notExisting() {
        every { persistence.deletePartner(-1) } throws ResourceNotFoundException("partner")
        assertThrows<ResourceNotFoundException> { deleteInteractor.deletePartner(-1) }
    }
}
