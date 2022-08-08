package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ControllerInstitutionUserRepository: JpaRepository<ControllerInstitutionUserEntity, ControllerInstitutionUserId> {
    @Query(
        """
            SELECT e FROM #{#entityName} e
            WHERE e.id.controllerInstitutionId=:institutionId AND e.id.user.id=:userId
        """
    )
    fun findByInstitutionIdAndUserId(institutionId: Long, userId: Long): Optional<ControllerInstitutionUserEntity>

    @Query(
        """
            SELECT e FROM #{#entityName} e
            WHERE e.id.user.id=:userId
        """
    )
    fun findAllByUserId(userId: Long): List<ControllerInstitutionUserEntity>

    @Query(
        """
            SELECT e FROM #{#entityName} e
            WHERE e.id.controllerInstitutionId=:controllerInstitutionId
        """
    )
    fun findAllByControllerInstitutionId(controllerInstitutionId: Long): List<ControllerInstitutionUserEntity>

    @Query(
        """
            SELECT e FROM #{#entityName} e
            WHERE e.id.controllerInstitutionId IN :institutionIds
        """
    )
    fun findAllByControllerInstitutionIdIn(institutionIds: Set<Long>): List<ControllerInstitutionUserEntity>

    fun deleteAllByIdControllerInstitutionIdAndIdUserIdIn(institutionId: Long, userIds: Set<Long>)
}
