package io.cloudflight.jems.server.project.service.sharedFolder.list

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.sharedFolderFile.list.ListSharedFolderFiles
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ListSharedFolderFilesTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 567L
        private const val EXPECTED_PATH = "Project/000567/SharedFolder/"
    }

    @MockK
    private lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    private lateinit var interactor: ListSharedFolderFiles

    @BeforeEach
    fun setUp() {
        clearMocks(filePersistence)
    }

    @Test
    fun list() {
        val files = mockk<Page<JemsFile>>()
        val pageable = mockk<Pageable>()
        every { filePersistence.listAttachments(pageable, EXPECTED_PATH, any(), any()) } returns files

        assertThat(interactor.list(PROJECT_ID, pageable)).isEqualTo(files)
    }

}
