package io.cloudflight.ems.service

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.api.dto.OutputUserRole
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.repository.AuditRepository
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertLinesMatch
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory


class AuditServiceTest {

    val user = LocalCurrentUser(OutputUser(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "ADMIN")
    ), "", emptyList())

    private val EXPECTED_LOG = "AUDIT >>> PROJECT_SUBMISSION (projectId submitted-projectId, user admin@admin.dev) : submission of the project application to the system"

    @MockK
    lateinit var auditRepository: AuditRepository

    lateinit var auditService: AuditService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    /**
     * Test audits, if they are saved in repository.
     */
    @Test
    fun testLogEvent_repository() {
        val event = slot<Audit>()
        every { auditRepository.save(capture(event)) } returnsArgument 0
        auditService = AuditServiceImpl(auditRepository)

        // test start
        auditService.logEvent(Audit.projectSubmitted(user, "submitted-projectId"))

        // assert
        assertEquals("submitted-projectId", event.captured.projectId)
    }

    /**
     * Test audits, if they are not saved in repository, but redirected to log.
     */
    @Test
    fun testLogEvent_logger() {
        auditService = AuditServiceLoggerImpl()

        val logger: Logger = LoggerFactory.getLogger(AuditServiceLoggerImpl::class.java) as Logger
        val listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()
        logger.addAppender(listAppender)

        // test start
        auditService.logEvent(Audit.projectSubmitted(user, "submitted-projectId"))

        // assert
        assertLinesMatch(listOf(EXPECTED_LOG), listAppender.list.map { it.formattedMessage })
        assertIterableEquals(listOf(Level.INFO), listAppender.list.map { it.level })
    }
}
