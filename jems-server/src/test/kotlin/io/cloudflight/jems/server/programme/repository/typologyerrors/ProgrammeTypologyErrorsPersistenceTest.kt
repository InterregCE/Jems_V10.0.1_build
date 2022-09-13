package io.cloudflight.jems.server.programme.repository.typologyerrors

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.typologyerrors.ProgrammeTypologyErrorsEntity
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProgrammeTypologyErrorsPersistenceTest: UnitTest() {

    companion object {
        private const val ID = 1L

        private val typologyErrorsEntity = ProgrammeTypologyErrorsEntity(id = ID, description = "Sample description")

        private val typologyErrors = TypologyErrors(
            id = ID,
            description = "Sample description",
        )
    }

    @MockK
    lateinit var repository: ProgrammeTypologyErrorsRepository


    @InjectMockKs
    private lateinit var persistence: ProgrammeTypologyErrorsPersistenceProvider

    @Test
    fun `get typology errors`() {
        every { repository.findAll() } returns listOf(typologyErrorsEntity)
        assertThat(persistence.getAllTypologyErrors())
            .containsExactly(typologyErrors)
    }

    @Test
    fun `update typology errors`() {
        every { repository.deleteAllByIdInBatch(listOf(2, 3)) } answers { }
        every { repository.saveAll(any<List<ProgrammeTypologyErrorsEntity>>()) } returnsArgument 0
        every { repository.findAll() } returns listOf(typologyErrorsEntity)

        assertThat(
            persistence.updateTypologyErrors(
                toDeleteIds = listOf(2, 3),
                toPersist = listOf(typologyErrors.copy(id = 0))
            )
        ).containsExactly(typologyErrors)

        verify(exactly = 1) { repository.deleteAllByIdInBatch(listOf(2, 3)) }
    }
}
