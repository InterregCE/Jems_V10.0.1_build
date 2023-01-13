package io.cloudflight.jems.server.project.service.contracting.partner.getPartners

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.data.domain.Sort

interface GetContractingPartnersInteractor {

    fun findAllByProjectIdForContracting(projectId: Long, sort: Sort, version: String? = null): List<ProjectPartnerSummary>
}
