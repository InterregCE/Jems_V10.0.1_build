package io.cloudflight.jems.server.service

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.resources.repository.GetLogosPersistenceProvider
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class GetLogosServiceTest : UnitTest() {

    private val bucket = "jems-logo-file-bucket"
    private val logo = "InterregProgrammeLogo_48.png"

    @RelaxedMockK
    lateinit var minioStorage: MinioStorage

    @InjectMockKs
    lateinit var getLogosPersistence: GetLogosPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(minioStorage)
    }

    @Test
    fun `get logos from empty bucket`() {
        every { minioStorage.exists(bucket, logo) } returns false
        assertThat(getLogosPersistence.getLogos()).isEmpty()
    }

    @Test
    fun `get logos from bucket`() {
        every { minioStorage.exists(bucket, logo) } returns true
        every { minioStorage.getFile(bucket, logo) } returns ByteArray(0)

        assertThat(getLogosPersistence.getLogos()).isNotEmpty()
        assertThat(getLogosPersistence.getLogos().first().logoType?.key).isEqualTo(logo)

    }

}
