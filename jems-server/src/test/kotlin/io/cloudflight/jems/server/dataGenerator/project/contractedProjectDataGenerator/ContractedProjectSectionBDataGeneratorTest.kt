package io.cloudflight.jems.server.dataGenerator.project.contractedProjectDataGenerator

import io.cloudflight.jems.api.project.ProjectAssociatedOrganizationApi
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.partner.ProjectPartnerApi
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ASSOCIATED_ORGANIZATION
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_LP
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_PP
import io.cloudflight.jems.server.dataGenerator.PROJECT_DATA_INITIALIZER_ORDER
import io.cloudflight.jems.server.dataGenerator.project.FIRST_VERSION
import io.cloudflight.jems.server.dataGenerator.project.projectPartnerDTO
import io.cloudflight.jems.server.dataGenerator.project.versionedInputTranslation
import io.cloudflight.jems.server.dataGenerator.project.versionedString
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.boot.web.server.LocalServerPort

@Order(PROJECT_DATA_INITIALIZER_ORDER + 20)
class ContractedProjectSectionBDataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

    private val projectPartnerApi =
        FeignTestClientFactory.createClientApi(ProjectPartnerApi::class.java, port, config)
    private val associatedOrganizationApi =
        FeignTestClientFactory.createClientApi(ProjectAssociatedOrganizationApi::class.java, port, config)

    @Test
    @Order(1)
    @ExpectSelect(24)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should add a partner to project`() {
        assertThat(
            projectPartnerApi.createProjectPartner(
                CONTRACTED_PROJECT_ID,
                projectPartnerDTO(version = FIRST_VERSION, abbreviation = "pp")
            )
                .also { CONTRACTED_PROJECT_PP = it }
        ).isNotNull
    }

    @Test
    @Order(1)
    @ExpectSelect(24)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should add a lead partner to project`() {
        assertThat(
            projectPartnerApi.createProjectPartner(
                CONTRACTED_PROJECT_ID,
                projectPartnerDTO(
                    version = FIRST_VERSION,
                    role = ProjectPartnerRoleDTO.LEAD_PARTNER,
                    abbreviation = "lp"
                )
            ).also { CONTRACTED_PROJECT_LP = it }
        ).isNotNull
    }

    @Test
    @Order(2)
    @ExpectSelect(15)
    @ExpectInsert(5)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should add associated organization to project`() {
        assertThat(
            associatedOrganizationApi.createAssociatedOrganization(
                CONTRACTED_PROJECT_ID, InputProjectAssociatedOrganization(
                    id = null,
                    partnerId = CONTRACTED_PROJECT_PP.id,
                    nameInOriginalLanguage = versionedString("name in original language", FIRST_VERSION),
                    nameInEnglish = versionedString("name in english", FIRST_VERSION),
                    address = null,
                    contacts = emptySet(),
                    roleDescription = versionedInputTranslation("role description", FIRST_VERSION)
                )
            ).also { CONTRACTED_PROJECT_ASSOCIATED_ORGANIZATION = it }
        ).isNotNull
    }
}
