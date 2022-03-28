package io.cloudflight.jems.server.dataGenerator

import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorDetailDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorDetailDTO
import io.cloudflight.jems.api.programme.dto.language.ProgrammeLanguageDTO
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityDTO
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.lumpsum.ProjectLumpSumDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.cloudflight.jems.api.project.dto.workpackage.ProjectWorkPackageDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectVersion

lateinit var SELECTED_PROGRAMME_FUNDS: List<ProgrammeFundDTO>
lateinit var PROGRAMME_STRATEGIES: List<OutputProgrammeStrategy>
lateinit var PROGRAMME_LUMP_SUMS: List<ProgrammeLumpSumDTO>
lateinit var PROGRAMME_UNIT_COSTS: List<ProgrammeUnitCostDTO>
lateinit var PROGRAMME_PRIORITY: ProgrammePriorityDTO
lateinit var PROGRAMME_UI_LANGUAGES: List<ProgrammeLanguageDTO>
lateinit var PROGRAMME_INPUT_LANGUAGES: List<ProgrammeLanguageDTO>
lateinit var PROGRAMME_OUTPUT_INDICATOR: OutputIndicatorDetailDTO
lateinit var PROGRAMME_RESULT_INDICATOR: ResultIndicatorDetailDTO
lateinit var PROGRAMME_LEGAL_STATUSES: List<ProgrammeLegalStatusDTO>

var STANDARD_CALL_LENGTH_OF_PERIOD: Int = 6
lateinit var STANDARD_CALL_DETAIL: CallDetailDTO

var CONTRACTED_PROJECT_ID = 0L
const val CONTRACTED_PROJECT_DURATION = 32
val CONTRACTED_PROJECT_PERIODS =
    listOf(
        ProjectPeriod(number = 1, start = 1, end = 6),
        ProjectPeriod(number = 2, start = 7, end = 12),
        ProjectPeriod(number = 3, start = 13, end = 18),
        ProjectPeriod(number = 4, start = 19, end = 24),
        ProjectPeriod(number = 5, start = 25, end = 30),
        ProjectPeriod(number = 6, start = 31, end = 32)
    )
lateinit var CONTRACTED_PROJECT_PP: ProjectPartnerDetailDTO
lateinit var CONTRACTED_PROJECT_LP: ProjectPartnerDetailDTO
lateinit var CONTRACTED_PROJECT_ASSOCIATED_ORGANIZATION: OutputProjectAssociatedOrganizationDetail
lateinit var CONTRACTED_PROJECT_WORK_PACKAGES: List<ProjectWorkPackageDTO>
lateinit var CONTRACTED_PROJECT_INVESTMENTS: List<WorkPackageInvestmentDTO>
lateinit var CONTRACTED_PROJECT_LUMP_SUMS: List<ProjectLumpSumDTO>
lateinit var CONTRACTED_PROJECT: ProjectDetail
lateinit var CONTRACTED_PROJECT_VERSIONS: List<ProjectVersion>


var DRAFT_PROJECT_ID = 0L
const val DRAFT_PROJECT_DURATION = 32
lateinit var DRAFT_PROJECT_PP: ProjectPartnerDetailDTO
lateinit var DRAFT_PROJECT_LP: ProjectPartnerDetailDTO
lateinit var DRAFT_PROJECT_ASSOCIATED_ORGANIZATION: OutputProjectAssociatedOrganizationDetail
lateinit var DRAFT_PROJECT_WORK_PACKAGES: List<ProjectWorkPackageDTO>
lateinit var DRAFT_PROJECT_INVESTMENTS: List<WorkPackageInvestmentDTO>
lateinit var DRAFT_PROJECT_LUMP_SUMS: List<ProjectLumpSumDTO>
