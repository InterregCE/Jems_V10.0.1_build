package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.project.dto.ProjectContactDTO
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.CreateProjectPartnerRequestDTO
import io.cloudflight.jems.api.project.dto.partner.UpdateProjectPartnerRequestDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface PartnerPersistence {

    fun throwIfNotExistsInProject(projectId: Long, partnerId: Long)

    fun findAllByProjectId(projectId: Long, page: Pageable, version: String? = null): Page<ProjectPartnerSummaryDTO>

    fun findAllByProjectId(projectId: Long): Iterable<ProjectPartnerDetailDTO>

    fun getById(id: Long, version: String? = null): ProjectPartnerDetailDTO

    fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort, version: String? = null): List<ProjectPartnerSummaryDTO>

    // used for authorization
    fun getProjectIdForPartnerId(id: Long, version: String? = null): Long

    fun create(projectId: Long, projectPartner: CreateProjectPartnerRequestDTO): ProjectPartnerDetailDTO

    fun update(projectPartner: UpdateProjectPartnerRequestDTO): ProjectPartnerDetailDTO

    fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): ProjectPartnerDetailDTO

    fun updatePartnerContacts(partnerId: Long, contacts: Set<ProjectContactDTO>): ProjectPartnerDetailDTO

    fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): ProjectPartnerDetailDTO

    fun getPartnerStateAid(partnerId: Long, version: String? = null): ProjectPartnerStateAid

    fun updatePartnerStateAid(partnerId: Long, stateAid: ProjectPartnerStateAid): ProjectPartnerStateAid

    fun deletePartner(partnerId: Long)
}
