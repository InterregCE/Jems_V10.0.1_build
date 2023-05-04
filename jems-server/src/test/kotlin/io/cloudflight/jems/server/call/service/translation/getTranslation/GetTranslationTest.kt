package io.cloudflight.jems.server.call.service.translation.getTranslation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.translation.CallTranslationFile
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

class GetTranslationTest : UnitTest() {

    companion object { private val yesterday = ZonedDateTime.now().minusDays(1) }

    @MockK private lateinit var filePersistence: JemsFilePersistence
    @MockK private lateinit var translationFilePersistence: TranslationFilePersistence

    @InjectMockKs private lateinit var interactor: GetTranslation

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence)
    }

    @Test
    fun get() {
        val translationNO = JemsFile(id = 502L, name = "call-id-7-Application_no.properties",
            type = JemsFileType.CallTranslation, uploaded = yesterday, author = mockk(),
            size = 222L, description = SystemLanguage.NO.name)

        every { filePersistence.listAttachments(Pageable.ofSize(34), "CallTranslation/000412/",
            setOf(JemsFileType.CallTranslation), emptySet()) } returns PageImpl(listOf(translationNO))
        every { translationFilePersistence.listTranslationFiles() } returns listOf(
            TranslationFileMetaData(SystemLanguage.NO, TranslationFileType.System, ZonedDateTime.now()),
            TranslationFileMetaData(SystemLanguage.NO, TranslationFileType.Application, ZonedDateTime.now()),
            TranslationFileMetaData(SystemLanguage.PL, TranslationFileType.Application, ZonedDateTime.now()),
        )

        val result = interactor.get(412L)
        assertThat(result).hasSize(34)
        assertThat(result.filter { it.file != null }).hasSize(1)
        assertThat(result.first { it.file != null }).isEqualTo(
            CallTranslationFile(
                SystemLanguage.NO,
                JemsFileMetadata(502L, "call-id-7-Application_no.properties", yesterday),
                "Application_no.properties",
            )
        )
        assertThat(result.mapTo(HashSet()) { it.language }).containsAll(SystemLanguage.values().toSet())
    }

}
