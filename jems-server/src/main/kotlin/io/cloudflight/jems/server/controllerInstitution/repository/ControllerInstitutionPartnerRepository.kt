package io.cloudflight.jems.server.controllerInstitution.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerSearchRequest
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.entity.QControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface ControllerInstitutionPartnerRepository: JpaRepository<ControllerInstitutionPartnerEntity, Long>,
    QuerydslPredicateExecutor<ControllerInstitutionPartnerEntity> {

    companion object {
        private val controllerInstitutionPartner = QControllerInstitutionPartnerEntity.controllerInstitutionPartnerEntity

        private fun likeCallIdentifier(callId: Long?) =
            if (callId == null) null
            else controllerInstitutionPartner.partner.project.call.id.eq(callId)

        private fun likeProjectIdentifier(id: String?) =
            if (id.isNullOrBlank()) null
            else {
                if (id.any { it.isLetter() })
                    controllerInstitutionPartner.projectIdentifier.like("%${id}%")
                else
                    controllerInstitutionPartner.projectIdentifier.like("%${id}%").or(controllerInstitutionPartner.partner.project.id.eq(id.toLong()))
            }

        private fun likeAcronym(acronym: String?) =
            if (acronym.isNullOrBlank()) null
            else controllerInstitutionPartner.projectAcronym.likeIgnoreCase("%${acronym}%")

        private fun likePartnerName(partnerName: String?) =
            if (partnerName.isNullOrBlank()) null
            else controllerInstitutionPartner.partnerAbbreviation.likeIgnoreCase("%${partnerName}%")

        private fun restrictNuts3(nuts3: Set<String>?): BooleanExpression? {
            if (nuts3 == null)
                return null

            val nuts3toFilter = nuts3.filter { it.length == 5 }
            return controllerInstitutionPartner.addressNuts3Code.`in`(nuts3toFilter)
        }

        private fun restrictNuts(nuts: Set<String>?): Predicate? {
            val byLength = nuts?.groupBy { it.length }?.filterKeys { it in intArrayOf(2, 3, 4, 5) }?.mapValues { it.value.toSet() }
            if (byLength.isNullOrEmpty())
                return null

            val filtersByNutsLevels = byLength.map { (level, values) ->
                if (level == 5)
                    restrictNuts3(values)
                else
                    ExpressionUtils.anyOf(
                        values.map { controllerInstitutionPartner.addressNuts3Code.likeIgnoreCase("${it}%") }
                    )
            }

            return ExpressionUtils.anyOf(filtersByNutsLevels)
        }

        fun buildSearchPredicate(searchRequest: InstitutionPartnerSearchRequest): Predicate =
            ExpressionUtils.allOf(
                likeCallIdentifier(searchRequest.callId),
                likeProjectIdentifier(searchRequest.projectId),
                likeAcronym(searchRequest.acronym),
                likePartnerName(searchRequest.partnerName),
                restrictNuts3(searchRequest.globallyRestrictedNuts),
                restrictNuts(searchRequest.partnerNuts),
            ) ?: BooleanBuilder()
    }

    fun findAllByPartnerIdInAndInstitutionNotNull(partnerIds: Set<Long>): List<ControllerInstitutionPartnerEntity>

    fun findAllByInstitutionId(institutionId: Long): List<ControllerInstitutionPartnerEntity>

    @Query("""
        SELECT ciu.id.user.id
        FROM #{#entityName} AS cip
            INNER JOIN project_partner AS pp
                ON cip.partnerId = pp.id AND pp.project.id = :projectId
            LEFT JOIN controller_institution_user AS ciu
                ON ciu.id.controllerInstitutionId = cip.institution.id
    """)
    fun getRelatedUserIdsForProject(projectId: Long): Set<Long>

    @Query(
        """
        SELECT ciu.accessLevel
        FROM #{#entityName} AS cip
        INNER JOIN controller_institution_user AS ciu
            ON cip.institution.id = ciu.id.controllerInstitutionId
        WHERE cip.partnerId = :partnerId AND ciu.id.user.id = :userId
        """
    )
    fun getControllerUserAccessLevelForPartner(userId: Long, partnerId: Long): UserInstitutionAccessLevel?

    @Query("""
        SELECT new kotlin.Pair(pp.project.id, pp.id)
        FROM controller_institution_user AS ciu
            LEFT JOIN #{#entityName} AS cip
                ON ciu.id.controllerInstitutionId = cip.institution.id
            LEFT JOIN project_partner pp
                ON cip.partnerId = pp.id
        WHERE ciu.id.user.id = :userId
    """)
    fun getRelatedProjectIdsForUser(userId: Long): List<Pair<Long, Long>>


    // Returns institution-partner assignments to delete if project partners nuts do not match the institution nuts
    @Query(
        """
            SELECT e FROM #{#entityName} e
            INNER JOIN project_partner_address ppa
                ON e.partnerId = ppa.addressId.partnerId AND ppa.addressId.type = 'Organization'
             WHERE (ppa.address.nutsRegion3Code, ppa.address.countryCode) NOT IN
                (SELECT cin.id.nutsRegion3Id, nuts1.country.id FROM controller_institution_nuts AS cin
                  INNER JOIN nuts_region_3 AS nuts3
                             ON cin.id.nutsRegion3Id = nuts3.id
                  INNER JOIN nuts_region_2 nuts2
                             ON nuts3.region2.id = nuts2.id
                  INNER JOIN nuts_region_1 nuts1
                             ON nuts2.region1.id = nuts1.id
                  WHERE e.institution.id = cin.id.institutionId
                )
            AND e.partner.project.id = :projectId
        """
    )
    fun getInstitutionPartnerAssignmentsToDeleteByProjectId(projectId: Long): List<ControllerInstitutionPartnerEntity>

    fun deleteAllByPartnerProjectId(projectId: Long)
}
