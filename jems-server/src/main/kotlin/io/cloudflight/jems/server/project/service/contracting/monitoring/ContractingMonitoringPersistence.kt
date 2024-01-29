package io.cloudflight.jems.server.project.service.contracting.monitoring

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import java.time.LocalDate

interface ContractingMonitoringPersistence {

    fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoring

    fun getPartnerPaymentDate(projectId: Long): Map<Long, LocalDate>

    fun updateContractingMonitoring(contractMonitoring: ProjectContractingMonitoring): ProjectContractingMonitoring

    fun updateClosureDate(projectId: Long, closureDate: LocalDate?): LocalDate?

    fun updatePartnerPaymentDate(projectId: Long, datePerPartner: Map<Long, LocalDate?>): Map<Long, LocalDate>

    fun existsSavedInstallment(projectId: Long, lumpSumId: Long, orderNr: Int): Boolean

}
