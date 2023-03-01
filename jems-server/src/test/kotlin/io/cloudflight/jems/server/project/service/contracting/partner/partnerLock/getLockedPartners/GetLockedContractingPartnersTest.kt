package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.getLockedPartners

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.ContractingPartnerLockPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetLockedContractingPartnersTest : UnitTest() {

    @MockK
    lateinit var persistence: ContractingPartnerLockPersistence

    @InjectMockKs
    lateinit var interactor: GetLockedContractingPartners

    @Test
    fun getLockedPartners() {
        val projectId = 1L
        every { persistence.getLockedPartners(projectId) } returns setOf(3L, 5L)

        Assertions.assertThat(interactor.getLockedPartners(projectId)).isEqualTo(setOf(3L, 5L))
    }

    @Test
    fun getLockedPartnersForNonExistentProject() {
        val invalidProjectId = -1L
        val exception = GetLockedContractingPartnersException(Exception())
        every { persistence.getLockedPartners(invalidProjectId) } throws exception

        assertThrows<GetLockedContractingPartnersException> { interactor.getLockedPartners(invalidProjectId) }
    }
}
