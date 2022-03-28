package io.cloudflight.jems.server

import io.cloudflight.jems.server.dataGenerator.project.ApplicationFormDataUpdateHelper
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.lumpsum.update_project_lump_sums.UpdateProjectLumpSumsInteractor
import io.cloudflight.jems.server.project.service.partner.update_project_partner.UpdateProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.result.update_project_results.UpdateProjectResultsInteractor
import io.cloudflight.jems.server.project.service.update_project_description.UpdateProjectDescriptionInteractor
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageServiceImpl
import io.cloudflight.jems.server.project.service.workpackage.activity.update_activity.UpdateActivityInteractor
import io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment.UpdateWorkPackageInvestmentInteractor
import io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output.UpdateWorkPackageOutputInteractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.annotation.Commit


@Commit
class DataGeneratorTest : IntegrationTest() {

    @Autowired
    private lateinit var projectService: ProjectService

    @Autowired
    private lateinit var updateProjectPartner: UpdateProjectPartnerInteractor

    @Autowired
    private lateinit var updateProjectDescription: UpdateProjectDescriptionInteractor

    @Autowired
    private lateinit var updateProjectResults: UpdateProjectResultsInteractor

    @Autowired
    private lateinit var updateProjectWorkPackage: WorkPackageServiceImpl

    @Autowired
    private lateinit var updateActivity: UpdateActivityInteractor

    @Autowired
    private lateinit var updateInvestment: UpdateWorkPackageInvestmentInteractor

    @Autowired
    private lateinit var updateWorkPackageOutput: UpdateWorkPackageOutputInteractor

    fun updateVersionsInApplicationFormInputs(projectId: Long, version: String) {
        if (SecurityContextHolder.getContext().authentication == null)
            loginAsAdmin()
        ApplicationFormDataUpdateHelper(
            projectService, updateProjectPartner, updateProjectDescription,
            updateProjectResults, updateProjectWorkPackage, updateActivity,
            updateInvestment, updateWorkPackageOutput
        ).updateVersionInFormInputs(projectId, version)
    }


}
