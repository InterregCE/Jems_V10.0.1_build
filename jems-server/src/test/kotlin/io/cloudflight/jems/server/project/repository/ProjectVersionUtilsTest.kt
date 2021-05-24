package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.sql.Timestamp
import java.time.LocalDateTime

internal class ProjectVersionUtilsTest : UnitTest() {

    private val projectId = 11L
    private val version = "1.0"
    private val timestamp = Timestamp.valueOf(LocalDateTime.now())
    private val currentVersionFetcher = { "currentVersion" }
    private val previousVersionFetcher = { timestamp: Timestamp -> "previousVersion for timestamp: $timestamp" }

    @MockK
    lateinit var projectVersionRepository: ProjectVersionRepository

    @InjectMockKs
    lateinit var projectVersionUtils: ProjectVersionUtils

    @Test
    fun `should fetch current version when version is null`() {

        every { projectVersionRepository.findTimestampByVersion(projectId, version) } returns timestamp
        val result = projectVersionUtils.fetch(null, projectId, currentVersionFetcher, previousVersionFetcher)
        assertThat(result).isEqualTo("currentVersion")
    }

    @Test
    fun `should fetch previous version when version is provided`() {

        every { projectVersionRepository.findTimestampByVersion(projectId, version) } returns timestamp
        val result = projectVersionUtils.fetch(version, projectId, currentVersionFetcher, previousVersionFetcher)
        assertThat(result).isEqualTo("previousVersion for timestamp: $timestamp")
    }
}
