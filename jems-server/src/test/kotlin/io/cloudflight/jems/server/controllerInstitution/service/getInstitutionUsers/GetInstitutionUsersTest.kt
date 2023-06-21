package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUsers

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetInstitutionUsersTest: UnitTest() {
    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    lateinit var getInstitutionUsers: GetInstitutionUsers

    @Test
    fun getInstitutionUsers() {
        val user = mockk<UserSimple>()
        every { controllerInstitutionPersistence.getControllerUsersForReportByInstitutionId(
            institutionId = 1L
        ) } returns listOf(user)
        assertThat(getInstitutionUsers.getInstitutionUsers(1L, 1L)).containsExactly(user)
    }
}
