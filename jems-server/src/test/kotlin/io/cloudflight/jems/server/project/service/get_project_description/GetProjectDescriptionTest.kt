package io.cloudflight.jems.server.project.service.get_project_description

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectDescriptionTest : UnitTest() {

    companion object {
        val projectDescription: ProjectDescription = mockk()
    }

    @MockK
    lateinit var persistence: ProjectDescriptionPersistence

    @InjectMockKs
    lateinit var getProjectDescription: GetProjectDescription

    @Test
    fun getProjectDescription() {
        every { persistence.getProjectDescription(1L, "v") } returns projectDescription
        assertThat(getProjectDescription.getProjectDescription(1L, "v")).isEqualTo(projectDescription)
    }

}
