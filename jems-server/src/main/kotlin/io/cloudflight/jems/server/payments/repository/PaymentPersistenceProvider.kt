package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.entity.PaymentToProjectEntity
import io.cloudflight.jems.server.payments.entity.toEntity
import io.cloudflight.jems.server.payments.entity.toListModel
import io.cloudflight.jems.server.payments.service.model.ComputedPaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.lumpsum.ProjectLumpSumRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PaymentPersistenceProvider(private val paymentRepository: PaymentRepository,
                                 private val projectRepository: ProjectRepository,
                                 private val projectLumpSumRepository: ProjectLumpSumRepository,
                                 private val fundRepository: ProgrammeFundRepository
                                 ): PaymentPersistence {

    @Transactional(readOnly = true)
    override fun getAllPaymentToProject(pageable: Pageable): Page<PaymentToProject> {
        return this.paymentRepository.getAllByGrouping(pageable).toListModel(
            getLumpSum = { projectId, orderNr -> projectLumpSumRepository.getByIdProjectIdAndIdOrderNr(projectId, orderNr) }
        )
    }

    @Transactional
    override fun deleteAllByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>): List<PaymentToProjectEntity> =
        this.paymentRepository.deleteAllByProjectIdAndOrderNr(projectId, orderNr)

    @Transactional
    override fun deleteAllByProjectId(projectId: Long): List<PaymentToProjectEntity> =
        this.paymentRepository.deleteAllByProjectId(projectId)

    @Transactional
    override fun getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(
        projectId: Long,
        orderNrsToBeAdded: MutableSet<Int>
    ): List<ComputedPaymentToProject> =
        this.paymentRepository.getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, orderNrsToBeAdded).toListModel()


    @Transactional
    override fun savePaymentToProjects(projectId: Long, calculatedAmountsToBeAdded: List<ComputedPaymentToProject>) {
        val projectEntity = projectRepository.getById(projectId)
        val entities = calculatedAmountsToBeAdded.toEntity(
            project = projectEntity,
            getProgrammeFund = { fundRepository.getById(it) }
        )
        this.paymentRepository.saveAll(entities)
    }

}
