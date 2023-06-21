package io.cloudflight.jems.server.project.controller.contracting.partner.stateAid

import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnerStateAidApi
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidDeMinimisSectionDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidGberSectionDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidDeMinimisDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidGberDTO
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection.GetContractingPartnerStateAidDeMinimisInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.updateStateAidDeMinimisSection.UpdateContractingPartnerStateAidDeMinimisInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection.GetContractingPartnersStateAidGberInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.updateStateAidGberSection.UpdateContractingPartnerStateAidGberInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingPartnerStateAidController(
    private val getDeMinimisSectionInteractor: GetContractingPartnerStateAidDeMinimisInteractor,
    private val getGberSectionInteractor: GetContractingPartnersStateAidGberInteractor,
    private val updateDeMinimisSectionInteractor: UpdateContractingPartnerStateAidDeMinimisInteractor,
    private val updateGberSectionInteractor: UpdateContractingPartnerStateAidGberInteractor,
): ContractingPartnerStateAidApi {

    override fun getDeMinimisSection(partnerId: Long): ContractingPartnerStateAidDeMinimisSectionDTO? =
        this.getDeMinimisSectionInteractor.getDeMinimisSection(partnerId)?.toDto()

    override fun updateDeMinimisSection(
        partnerId: Long,
        deMinimisData: ContractingPartnerStateAidDeMinimisDTO
    ): ContractingPartnerStateAidDeMinimisSectionDTO =
        this.updateDeMinimisSectionInteractor.updateDeMinimisSection(partnerId, deMinimisData.toModel()).toDto()

    override fun getGberSection(partnerId: Long): ContractingPartnerStateAidGberSectionDTO? =
        this.getGberSectionInteractor.getGberSection(partnerId)?.toDto()

    override fun updateGberSection(
        partnerId: Long,
        gberData: ContractingPartnerStateAidGberDTO
    ): ContractingPartnerStateAidGberSectionDTO =
        this.updateGberSectionInteractor.updateGberSection(partnerId, gberData.toModel()).toDto()

}
