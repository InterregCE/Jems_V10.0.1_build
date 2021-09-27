package io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums

import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import java.math.BigDecimal

interface GetProjectLumpSumsInteractor {

    fun getLumpSums(projectId: Long, version: String? = null): List<ProjectLumpSum>

    fun getLumpSumsTotalForPartner(partnerId: Long, version: String? = null): BigDecimal

}
