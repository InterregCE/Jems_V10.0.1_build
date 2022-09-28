package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.service.model.PaymentPerPartner
import io.cloudflight.jems.server.payments.service.model.PaymentToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentToProject
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.lumpsum.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PaymentPersistenceProvider(private val paymentRepository: PaymentRepository,
                                 private val paymentPartnerRepository: PaymentPartnerRepository,
                                 private val projectRepository: ProjectRepository,
                                 private val projectLumpSumRepository: ProjectLumpSumRepository,
                                 private val projectPersistence: ProjectPersistence,
                                 private val fundRepository: ProgrammeFundRepository): PaymentPersistence {

    @Transactional(readOnly = true)
    override fun getAllPaymentToProject(pageable: Pageable): Page<PaymentToProject> {
        return paymentRepository.findAll(pageable).toListModel(
            getLumpSum = { projectId, orderNr -> projectLumpSumRepository.getByIdProjectIdAndIdOrderNr(projectId, orderNr) },
            getProject = { projectId, version -> projectPersistence.getProject(projectId, version) }
        )
    }

    @Transactional
    override fun deleteAllByProjectIdAndOrderNrIn(projectId: Long, orderNr: Set<Int>) {
        this.paymentRepository.deleteAllByProjectIdAndOrderNr(projectId, orderNr)
    }

    @Transactional
    override fun getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(
        projectId: Long,
        orderNrsToBeAdded: MutableSet<Int>
    ): List<PaymentPerPartner> =
        this.paymentRepository
            .getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, orderNrsToBeAdded)
            .toListModel()

    @Transactional
    override fun savePaymentToProjects(projectId: Long, paymentsToBeSaved: Map<PaymentGroupingId, PaymentToCreate>) {
        val projectEntity = projectRepository.getById(projectId)
        val paymentEntities = this.paymentRepository.saveAll(paymentsToBeSaved.map { (id, model) ->
            model.toEntity(projectEntity, id.orderNr, fundRepository.getById(id.programmeFundId))
        }).associateBy { PaymentGroupingId(it.orderNr, it.fund.id) }

        paymentEntities.forEach { (paymentId, entity) ->
            paymentPartnerRepository.saveAll(
                paymentsToBeSaved[paymentId]!!.partnerPayments.map { it.toEntity(entity) }
            )
        }
    }
}
