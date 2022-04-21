package io.cloudflight.jems.server.dataGenerator.project.contractedProjectDataGenerator

import io.cloudflight.jems.api.project.ProjectStatusApi
import io.cloudflight.jems.api.project.dto.ApplicationActionInfoDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.CONTRACTED_PROJECT_ID
import io.cloudflight.jems.server.dataGenerator.PROJECT_DATA_INITIALIZER_ORDER
import io.cloudflight.jems.server.dataGenerator.project.versionedString
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.boot.web.server.LocalServerPort
import java.time.LocalDate


@Order(PROJECT_DATA_INITIALIZER_ORDER + 50)
class ContractedProjectWorkflowDataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

    private val projectStatusApi =
        FeignTestClientFactory.createClientApi(ProjectStatusApi::class.java, port, config)

    private var version: String = "1.0"

    @Test
    @Order(10)
    @ExpectSelect(23)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `submit application`() {
        assertThat(projectStatusApi.submitApplication(CONTRACTED_PROJECT_ID)).isEqualTo(
            ApplicationStatusDTO.SUBMITTED
        )
    }

    @Test
    @Order(20)
    @ExpectSelect(11)
    @ExpectInsert(2)
    @ExpectUpdate(2)
    @ExpectDelete(1)
    fun `return the application to applicant`() {
        assertThat(
            projectStatusApi.returnApplicationToApplicant(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.RETURNED_TO_APPLICANT)

    }

    @Test
    @Order(21)
    fun `should increase version and set version in form inputs to 2`() {
        increaseVersionAndUpdateFormInputs()
    }

    @Test
    @Order(30)
    @ExpectSelect(23)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `resubmit the application`() {
        assertThat(
            projectStatusApi.submitApplication(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.SUBMITTED)
    }

    @Test
    @Order(40)
    @ExpectSelect(26)
    @ExpectInsert(1)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `enter application eligibility decision`() {
        assertThat(
            projectStatusApi.setEligibilityAssessment(
                CONTRACTED_PROJECT_ID,
                ProjectAssessmentEligibilityDTO(
                    ProjectAssessmentEligibilityResult.PASSED,
                    versionedString("note", version)
                )
            )
        ).isNotNull
    }

    @Test
    @Order(50)
    @ExpectSelect(26)
    @ExpectInsert(1)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `set application quality assessment`() {
        assertThat(
            projectStatusApi.setQualityAssessment(
                CONTRACTED_PROJECT_ID,
                ProjectAssessmentQualityDTO(
                    ProjectAssessmentQualityResult.RECOMMENDED_FOR_FUNDING,
                    versionedString("note", version)
                )
            )
        ).isNotNull
    }

    @Test
    @Order(60)
    @ExpectSelect(25)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `set application as eligible`() {
        assertThat(
            projectStatusApi.setApplicationAsEligible(
                CONTRACTED_PROJECT_ID,
                ApplicationActionInfoDTO(versionedString("note", version), LocalDate.now(), null)
            )
        ).isEqualTo(ApplicationStatusDTO.ELIGIBLE)
    }

    @Test
    @Order(70)
    @ExpectSelect(9)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `approve application`() {
        assertThat(
            projectStatusApi.approveApplication(
                CONTRACTED_PROJECT_ID,
                ApplicationActionInfoDTO(versionedString("note", version), LocalDate.now(), null)
            )
        ).isEqualTo(ApplicationStatusDTO.APPROVED)
    }

    @Test
    @Order(80)
    @ExpectSelect(11)
    @ExpectInsert(2)
    @ExpectUpdate(2)
    @ExpectDelete(1)
    fun `start the first modification for application`() {
        assertThat(
            projectStatusApi.startModification(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.MODIFICATION_PRECONTRACTING)
    }

    @Test
    @Order(81)
    fun `should increase version and set version in form inputs to 3`() {
        increaseVersionAndUpdateFormInputs()
    }

    @Test
    @Order(90)
    @ExpectSelect(23)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `submit the application after the modification`() {
        assertThat(
            projectStatusApi.submitApplication(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.MODIFICATION_PRECONTRACTING_SUBMITTED)
    }

    @Test
    @Order(100)
    @ExpectSelect(14)
    @ExpectUpdate(4)
    fun `reject the first modification of the application`() {
        assertThat(
            projectStatusApi.rejectModification(
                CONTRACTED_PROJECT_ID,
                ApplicationActionInfoDTO(versionedString("note", version), LocalDate.now(), LocalDate.now().plusDays(2))
            )
        ).isEqualTo(ApplicationStatusDTO.MODIFICATION_REJECTED)
    }


    @Test
    @Order(110)
    @ExpectSelect(11)
    @ExpectInsert(2)
    @ExpectUpdate(2)
    @ExpectDelete(1)
    fun `start second modification of the application`() {
        assertThat(
            projectStatusApi.startModification(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.MODIFICATION_PRECONTRACTING)
    }

    @Test
    @Order(111)
    fun `should increase version and set version in form inputs to 4`() {
        increaseVersionAndUpdateFormInputs()
    }

    @Test
    @Order(120)
    @ExpectSelect(23)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `submit the second modification of the application`() {
        assertThat(
            projectStatusApi.submitApplication(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.MODIFICATION_PRECONTRACTING_SUBMITTED)
    }

    @Test
    @Order(130)
    @ExpectSelect(8)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `approve second modification of the application`() {
        assertThat(
            projectStatusApi.approveModification(
                CONTRACTED_PROJECT_ID, ApplicationActionInfoDTO(versionedString("note", version), LocalDate.now(), null)
            )
        ).isEqualTo(
            ApplicationStatusDTO.APPROVED
        )
    }

    @Test
    @Order(140)
    @ExpectSelect(8)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `set the application as contracted`() {
        assertThat(
            projectStatusApi.setToContracted(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.CONTRACTED)
    }

    @Test
    @Order(150)
    @ExpectSelect(11)
    @ExpectInsert(2)
    @ExpectUpdate(2)
    @ExpectDelete(1)
    fun `start third modification on the contracted application`() {
        assertThat(
            projectStatusApi.startModification(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.IN_MODIFICATION)
    }

    @Test
    @Order(151)
    fun `should increase version and set version in form inputs to 5`() {
        increaseVersionAndUpdateFormInputs()
    }

    @Test
    @Order(160)
    @ExpectSelect(23)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `submit the third modification`() {
        assertThat(
            projectStatusApi.submitApplication(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.MODIFICATION_SUBMITTED)
    }

    @Test
    @Order(170)
    @ExpectSelect(8)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `handback the third modification of application`() {
        assertThat(
            projectStatusApi.handBackToApplicant(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.IN_MODIFICATION)
    }

    @Test
    @Order(180)
    @ExpectSelect(23)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `submit the third modification after it was handed back to the applicant`() {
        assertThat(
            projectStatusApi.submitApplication(CONTRACTED_PROJECT_ID)
        ).isEqualTo(ApplicationStatusDTO.MODIFICATION_SUBMITTED)
    }

    @Test
    @Order(190)
    @ExpectSelect(8)
    @ExpectInsert(1)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `approve the third modification after it was handed back to the applicant`() {
        assertThat(
            projectStatusApi.approveModification(
                CONTRACTED_PROJECT_ID,
                ApplicationActionInfoDTO(versionedString("note", version), LocalDate.now(), null)
            )
        ).isEqualTo(ApplicationStatusDTO.CONTRACTED)
    }

    private fun increaseVersionAndUpdateFormInputs() {
        version = ProjectVersionUtils.increaseMajor(version)
        updateVersionsInApplicationFormInputs(CONTRACTED_PROJECT_ID, version)
    }
}
