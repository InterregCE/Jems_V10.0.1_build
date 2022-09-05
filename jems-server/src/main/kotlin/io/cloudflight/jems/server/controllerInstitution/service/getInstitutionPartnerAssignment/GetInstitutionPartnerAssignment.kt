package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionPartnerAssignment

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.authorization.CanViewInstitutionAssignments
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetInstitutionPartnerAssignment(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence
) : GetInstitutionPartnerAssignmentInteractor {

    @CanViewInstitutionAssignments
    @ExceptionWrapper(GetInstitutionPartnerAssignmentException::class)
    @Transactional(readOnly = true)
    override fun getInstitutionPartnerAssignments(pageable: Pageable): Page<InstitutionPartnerDetails> {

        val institutionsWithNuts = controllerInstitutionPersistence.getAllControllerInstitutions()
        val institutions =
            institutionsWithNuts.associateBy(keySelector = { it.id }, valueTransform = { IdNamePair(it.id, it.name) })

        val countryToInstitutions: MutableMap<String, MutableSet<IdNamePair>> = mutableMapOf()
        val nuts3CodeToInstitutions = institutionsWithNuts.flatMap { institution ->
            val institutionDetails = institutions[institution.id]!!
            institution.institutionNuts.map { nutsRegion3 ->
                Pair(nutsRegion3.id, institutionDetails).also {
                    updateCountryToInstitutionsMap(
                        nutsRegion3.region2.region1.country.id,
                        institutionDetails,
                        countryToInstitutions
                    )
                }
            }
        }.groupBy(keySelector = { it.first }, valueTransform = { it.second })

        return controllerInstitutionPersistence.getInstitutionPartnerAssignments(pageable).onEach { partnerDetails ->
            partnerDetails.setAvailableInstitutionsForPartner(
                getAvailableInstitutionsForPartner(
                    partnerDetails,
                    nuts3CodeToInstitutions,
                    countryToInstitutions,
                    institutions.values.toList()
                )
            )
        }
    }

    private fun getAvailableInstitutionsForPartner(
        partnerDetails: InstitutionPartnerDetails,
        nuts3CodeToInstitutionsMap: Map<String, List<IdNamePair>>,
        countryCodeToInstitutionsMap: Map<String, MutableSet<IdNamePair>>,
        allInstitutions: List<IdNamePair>
    ): Set<IdNamePair> {
        if (!partnerDetails.partnerNuts3Code.isNullOrEmpty())
            return nuts3CodeToInstitutionsMap[partnerDetails.partnerNuts3Code]?.toSet() ?: emptySet()
        if (!partnerDetails.countryCode.isNullOrEmpty())
            return countryCodeToInstitutionsMap[partnerDetails.countryCode] ?: emptySet()
        return allInstitutions.toSet()
    }

    private fun InstitutionPartnerDetails.setAvailableInstitutionsForPartner(institutions: Set<IdNamePair>) {
        this.partnerNutsCompatibleInstitutions?.addAll(institutions)
    }

    private fun updateCountryToInstitutionsMap(
        countryCode: String,
        institutionDetails: IdNamePair,
        countryToInstitutionsMap: MutableMap<String, MutableSet<IdNamePair>>
    ) {
        if (countryToInstitutionsMap.containsKey(countryCode)) {
            countryToInstitutionsMap[countryCode]?.add(institutionDetails)
        } else {
            countryToInstitutionsMap[countryCode] = mutableSetOf(institutionDetails)
        }
    }

}
