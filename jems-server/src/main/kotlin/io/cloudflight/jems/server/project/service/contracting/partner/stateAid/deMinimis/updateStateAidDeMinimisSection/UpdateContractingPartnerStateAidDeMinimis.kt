package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.updateStateAidDeMinimisSection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartnerStateAid
import io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.toModel
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.ContractingPartnerStateAidDeMinimisPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateContractingPartnerStateAidDeMinimis(
    private val contractingPartnerStateAidDeMinimisPersistence: ContractingPartnerStateAidDeMinimisPersistence,
    private val getContractingMonitoringService: GetContractingMonitoringService,
    private val partnerPersistence: PartnerPersistence,
    private val versionPersistence: ProjectVersionPersistence,
    private val validator: ContractingValidator,
): UpdateContractingPartnerStateAidDeMinimisInteractor {

    @CanUpdateProjectContractingPartnerStateAid
    @Transactional
    @ExceptionWrapper(UpdateContractingPartnerStateAidDeMinimisException::class)
    override fun updateDeMinimisSection(
        partnerId: Long,
        deMinimisData: ContractingPartnerStateAidDeMinimis
    ): ContractingPartnerStateAidDeMinimisSection {
        validator.validatePartnerLock(partnerId)

        val updatedDeMinimis = this.contractingPartnerStateAidDeMinimisPersistence.saveDeMinimis(partnerId, deMinimisData)
        val projectId = this.partnerPersistence.getProjectIdForPartnerId(partnerId)
        val lastApprovedVersion = this.versionPersistence.getLatestApprovedOrCurrent(projectId)
        val partnerData = this.partnerPersistence.getById(partnerId, lastApprovedVersion)
        val addDates = getContractingMonitoringService.getProjectContractingMonitoring(partnerData.projectId).addDates

        return ContractingPartnerStateAidDeMinimisSection(
            partnerId = partnerId,
            dateOfGrantingAid = addDates.minByOrNull { addDate -> addDate.number }?.entryIntoForceDate,
            amountGrantingAid = updatedDeMinimis.amountGrantingAid ?: BigDecimal.ZERO,
            selfDeclarationSubmissionDate = updatedDeMinimis.selfDeclarationSubmissionDate,
            baseForGranting = updatedDeMinimis.baseForGranting,
            aidGrantedByCountry = updatedDeMinimis.aidGrantedByCountry,
            memberStatesGranting = updatedDeMinimis.memberStatesGranting.toModel(),
            comment = updatedDeMinimis.comment
        )
    }
}
