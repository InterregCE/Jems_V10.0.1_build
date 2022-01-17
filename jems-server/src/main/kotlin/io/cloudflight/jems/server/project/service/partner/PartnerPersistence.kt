package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface PartnerPersistence {

    fun throwIfNotExistsInProject(projectId: Long, partnerId: Long)

    fun findAllByProjectId(projectId: Long, page: Pageable, version: String? = null): Page<ProjectPartnerSummary>

    fun countByProjectId(projectId: Long): Long

    fun findTop30ByProjectId(projectId: Long, version: String? = null): Iterable<ProjectPartnerDetail>

    fun changeRoleOfLeadPartnerToPartnerIfItExists(projectId: Long)

    fun throwIfPartnerAbbreviationAlreadyExists(projectId: Long, abbreviation: String)

    fun getById(id: Long, version: String? = null): ProjectPartnerDetail

    fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort, version: String? = null): List<ProjectPartnerSummary>

    // used for authorization
    fun getProjectIdForPartnerId(id: Long, version: String? = null): Long

    fun create(projectId: Long, projectPartner: ProjectPartner, resortByRole: Boolean): ProjectPartnerDetail

    fun update(projectPartner: ProjectPartner, resortByRole: Boolean): ProjectPartnerDetail

    fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddress>): ProjectPartnerDetail

    fun updatePartnerContacts(partnerId: Long, contacts: Set<ProjectPartnerContact>): ProjectPartnerDetail

    fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivation): ProjectPartnerDetail

    fun getPartnerStateAid(partnerId: Long, version: String? = null): ProjectPartnerStateAid

    fun updatePartnerStateAid(partnerId: Long, stateAid: ProjectPartnerStateAid): ProjectPartnerStateAid

    fun deletePartner(partnerId: Long)

    fun deactivatePartner(partnerId: Long)
}
