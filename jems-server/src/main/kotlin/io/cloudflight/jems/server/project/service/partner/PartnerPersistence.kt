package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface PartnerPersistence {

    fun findAllByProjectId(projectId: Long, page: Pageable, version: String? = null): Page<OutputProjectPartner>

    fun findAllByProjectId(projectId: Long): Iterable<OutputProjectPartnerDetail>

    fun getById(id: Long, version: String? = null): OutputProjectPartnerDetail

    fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort, version: String? = null): List<OutputProjectPartner>

    // used for authorization
    fun getProjectIdForPartnerId(id: Long): Long

    fun create(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail

    fun update(projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail

    fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): OutputProjectPartnerDetail

    fun updatePartnerContacts(partnerId: Long, contacts: Set<InputProjectContact>): OutputProjectPartnerDetail

    fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): OutputProjectPartnerDetail

    fun deletePartner(partnerId: Long)
}
