package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.cofinancing.InputProjectPartnerCoFinancing
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.toOutputProjectPartnerDetail
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectPartnerCoFinancingServiceImpl(
    private val projectPartnerRepo: ProjectPartnerRepository
) : ProjectPartnerCoFinancingService {

    @Transactional
    override fun updatePartnerCoFinancing(partnerId: Long, financing: Set<InputProjectPartnerCoFinancing>): OutputProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(partnerId)
        val availableFundsForCall = projectPartner.project.call.funds.associateBy { it.id }

        validateOnlyFundsAvailableForCallAreUsed(financing, availableFundsForCall.keys)

        return projectPartnerRepo.save(
            projectPartner.copy(
                financing = financing.toEntity(projectPartner.id, availableFundsForCall)
            )
        ).toOutputProjectPartnerDetail()
    }

    private fun validateOnlyFundsAvailableForCallAreUsed(financing: Set<InputProjectPartnerCoFinancing>, allowedFundIds: Set<Long>) {
        val fundIds = financing
            .filter { it.fundId != null }
            .mapTo(HashSet()) { it.fundId!! }

        if (!allowedFundIds.containsAll(fundIds))
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.coFinancing.fundId.not.allowed.for.call"
            )
    }

    private fun getPartnerOrThrow(partnerId: Long): ProjectPartnerEntity {
        return projectPartnerRepo.findById(partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
    }

}
