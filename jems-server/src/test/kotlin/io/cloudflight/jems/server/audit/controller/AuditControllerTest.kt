package io.cloudflight.jems.server.audit.controller

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.audit.dto.AuditDTO
import io.cloudflight.jems.api.audit.dto.AuditProjectDTO
import io.cloudflight.jems.api.audit.dto.AuditSearchRequestDTO
import io.cloudflight.jems.api.audit.dto.AuditUserDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditFilter
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.model.AuditSearchRequest
import io.cloudflight.jems.server.audit.model.AuditUser
import io.cloudflight.jems.server.audit.service.get_audit.GetAuditInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable.unpaged
import java.time.ZonedDateTime

class AuditControllerTest: UnitTest() {

    companion object {

        private val time = ZonedDateTime.now()

        private val audit = Audit(
            id = "ID_of_audit",
            timestamp = time,
            action = AuditAction.QUALITY_ASSESSMENT_CONCLUDED,
            project = AuditProject(id = "PORJECT_ID_5", name = "Project name"),
            user = AuditUser(id = 8L, email = "user8@mail.eu"),
            description = "Quality assessment concluded by user user8@mail.eu",
        )

        private val auditDto = AuditDTO(
            timestamp = time,
            action = AuditAction.QUALITY_ASSESSMENT_CONCLUDED,
            project = AuditProjectDTO(id = "PORJECT_ID_5", name = "Project name"),
            user = AuditUserDTO(id = 8L, email = "user8@mail.eu"),
            description = "Quality assessment concluded by user user8@mail.eu",
        )

    }

    @MockK
    lateinit var getAudit: GetAuditInteractor

    @InjectMockKs
    private lateinit var controller: AuditController

    @Test
    fun getAudits() {
        val searchRequest = AuditSearchRequestDTO(
            userIds = setOf(8L),
            userEmails = setOf("user8@mail.eu"),
            actions = setOf(AuditAction.QUALITY_ASSESSMENT_CONCLUDED),
            projectIds = setOf("PROJECT_ID_5"),
        )
        val pageable = unpaged()
        val slot = slot<AuditSearchRequest>()
        every { getAudit.getAudit(capture(slot)) } returns PageImpl(listOf(audit))

        assertThat(controller.getAudits(pageable, searchRequest).content).containsExactly(auditDto)
        assertThat(slot.captured).isEqualTo(
            AuditSearchRequest(
                userId = AuditFilter(setOf(8L)),
                userEmail = AuditFilter(setOf("user8@mail.eu")),
                action = AuditFilter(setOf(AuditAction.QUALITY_ASSESSMENT_CONCLUDED)),
                projectId = AuditFilter(setOf("PROJECT_ID_5")),
                pageable = pageable
            )
        )
    }

}
