package io.cloudflight.jems.server.controllerInstitution.service.create_controller

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.CreateController
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class CreateControllerTest: UnitTest() {

    @RelaxedMockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var createController: CreateController

    private val INSTITUTION_ID = 1L
    private val institution = ControllerInstitution(
        id = INSTITUTION_ID,
        name = "INSTITUTION",
        description = "DESCRIPTION",
        institutionNuts = emptyList(),
        institutionUsers = emptyList(),
        createdAt = ZonedDateTime.now()
    )

    private val updateInstitution = UpdateControllerInstitution(
        id = INSTITUTION_ID,
        name = "INSTITUTION",
        description = "DESCRIPTION",
        institutionNuts = emptyList(),
        institutionUsers = emptyList(),
        createdAt = ZonedDateTime.now()
    )

    @Test
    fun createInstitution() {
        every {controllerInstitutionPersistence.createControllerInstitution(updateInstitution)} returns institution
        assertThat(createController.createController(updateInstitution)).isEqualTo(institution)
    }
}
