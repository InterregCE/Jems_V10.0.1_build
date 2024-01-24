package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingPartnerPaymentDate

import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosure
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureUpdate

interface UpdateContractingPartnerPaymentDateInteractor {

    fun updatePartnerPaymentDate(projectId: Long, closure: ContractingClosureUpdate): ContractingClosure

}
