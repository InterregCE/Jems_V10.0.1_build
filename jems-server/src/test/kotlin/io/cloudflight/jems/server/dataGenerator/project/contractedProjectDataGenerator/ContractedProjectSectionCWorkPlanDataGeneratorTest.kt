package io.cloudflight.jems.server.dataGenerator.project.contractedProjectDataGenerator

import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDeliverableDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageActivityApi
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageApi
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageInvestmentApi
import io.cloudflight.jems.api.project.workpackage.ProjectWorkPackageOutputApi
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_INVESTMENTS
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_WORK_PACKAGES
import io.cloudflight.jems.server.dataGenerator.PROGRAMME_OUTPUT_INDICATOR
import io.cloudflight.jems.server.dataGenerator.PROJECT_DATA_INITIALIZER_ORDER
import io.cloudflight.jems.server.dataGenerator.project.FIRST_VERSION
import io.cloudflight.jems.server.dataGenerator.project.inputWorkPackageCreate
import io.cloudflight.jems.server.dataGenerator.project.versionedInputTranslation
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.Ordered
import java.math.BigDecimal


@Order(PROJECT_DATA_INITIALIZER_ORDER + 31)
class ContractedProjectSectionCWorkPlanDataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

    private val workPackageApi =
        FeignTestClientFactory.createClientApi(ProjectWorkPackageApi::class.java, port, config)
    private val activityApi =
        FeignTestClientFactory.createClientApi(ProjectWorkPackageActivityApi::class.java, port, config)
    private val investmentApi =
        FeignTestClientFactory.createClientApi(ProjectWorkPackageInvestmentApi::class.java, port, config)
    private val outputApi =
        FeignTestClientFactory.createClientApi(ProjectWorkPackageOutputApi::class.java, port, config)

    private var workPackageId = 0L

    @Test
    @Order(3)
    @ExpectSelect(10)
    @ExpectInsert(5)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should add a new work plan to the project`() {
        assertThat(
            workPackageApi.createWorkPackage(CONTRACTED_PROJECT_ID, inputWorkPackageCreate(FIRST_VERSION))
                .also { workPackageId = it.id }
        ).isNotNull
    }

    @Test
    @Order(4)
    @ExpectSelect(33)
    @ExpectInsert(20)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should add activities to the work plan`() {
        assertThat(
            activityApi.updateActivities(
                CONTRACTED_PROJECT_ID, workPackageId, listOf(
                    WorkPackageActivityDTO(
                        id = 0,
                        workPackageId = workPackageId,
                        activityNumber = null,
                        title = versionedInputTranslation("title 1", FIRST_VERSION),
                        startPeriod = 1,
                        endPeriod = 2,
                        description = versionedInputTranslation("description 1", FIRST_VERSION),
                        deliverables = listOf(
                            WorkPackageActivityDeliverableDTO(
                                deliverableId = 0,
                                activityId = 0,
                                deliverableNumber = 0,
                                description = versionedInputTranslation("description 1-1", FIRST_VERSION),
                                title = versionedInputTranslation("title 1-1", FIRST_VERSION),
                                period = 1
                            )
                        )
                    ),
                    WorkPackageActivityDTO(
                        id = 0,
                        workPackageId = workPackageId,
                        activityNumber = null,
                        title = versionedInputTranslation("title 2", FIRST_VERSION),
                        startPeriod = 2,
                        endPeriod = 3,
                        description = versionedInputTranslation("description 2", FIRST_VERSION),
                        deliverables = listOf(
                            WorkPackageActivityDeliverableDTO(
                                deliverableId = 0,
                                activityId = 0,
                                deliverableNumber = 0,
                                description = versionedInputTranslation("description 2-1", FIRST_VERSION),
                                title = versionedInputTranslation("title 2-1", FIRST_VERSION),
                                period = 2
                            )
                        )
                    )
                )
            )
        ).isNotNull
    }

    @Test
    @Order(4)
    @ExpectSelect(30)
    @ExpectInsert(5)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should add investments to the work plan`() {
        assertThat(
            investmentApi.addWorkPackageInvestment(
                CONTRACTED_PROJECT_ID, workPackageId, WorkPackageInvestmentDTO(
                    id = null,
                    investmentNumber = 0,
                    expectedDeliveryPeriod = 1,
                    title = versionedInputTranslation("title", FIRST_VERSION),
                    justificationExplanation = versionedInputTranslation("justification explanation", FIRST_VERSION),
                    justificationTransactionalRelevance = versionedInputTranslation(
                        "justification transactional relevance",
                        FIRST_VERSION
                    ),
                    justificationBenefits = versionedInputTranslation("justification benefits", FIRST_VERSION),
                    justificationPilot = versionedInputTranslation("justification pilot", FIRST_VERSION),
                    address = null,
                    risk = versionedInputTranslation("risk", FIRST_VERSION),
                    documentation = versionedInputTranslation("documentation", FIRST_VERSION),
                    documentationExpectedImpacts = versionedInputTranslation(
                        "documentation expected impacts",
                        FIRST_VERSION
                    ),
                    ownershipSiteLocation = versionedInputTranslation("ownership site location", FIRST_VERSION),
                    ownershipRetain = versionedInputTranslation("ownership retain", FIRST_VERSION),
                    ownershipMaintenance = versionedInputTranslation("ownership maintenance", FIRST_VERSION)
                )
            )
        ).isNotNull
    }

    @Test
    @Order(4)
    @ExpectSelect(17)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should add outputs to the work plan`() {
        assertThat(
            outputApi.updateOutputs(
                CONTRACTED_PROJECT_ID, workPackageId, listOf(
                    WorkPackageOutputDTO(
                        workPackageId = workPackageId,
                        outputNumber = null,
                        programmeOutputIndicatorId = PROGRAMME_OUTPUT_INDICATOR.id,
                        programmeOutputIndicatorIdentifier = PROGRAMME_OUTPUT_INDICATOR.identifier,
                        targetValue = BigDecimal.valueOf(34211, 2),
                        periodNumber = 1,
                        title = versionedInputTranslation("title", FIRST_VERSION),
                        description = versionedInputTranslation("description", FIRST_VERSION)
                    )
                )
            )
        ).isNotNull
    }

    @Test
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExpectSelect(10)
    @ExpectInsert(0)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should return project work packages investments`() {
        CONTRACTED_PROJECT_INVESTMENTS = investmentApi.getWorkPackageInvestments(CONTRACTED_PROJECT_ID, workPackageId)

        assertThat(CONTRACTED_PROJECT_INVESTMENTS).isNotNull
    }

    @Test
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExpectSelect(11)
    @ExpectInsert(0)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should return project work packages`() {
        CONTRACTED_PROJECT_WORK_PACKAGES = workPackageApi.getWorkPackagesForTimePlanByProjectId(CONTRACTED_PROJECT_ID)

        assertThat(CONTRACTED_PROJECT_WORK_PACKAGES).isNotNull
    }


}
