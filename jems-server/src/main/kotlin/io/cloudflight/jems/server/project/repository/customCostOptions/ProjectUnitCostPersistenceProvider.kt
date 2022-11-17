package io.cloudflight.jems.server.project.repository.customCostOptions

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostBudgetCategoryEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostRow
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslId
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.costoption.toProgrammeUnitCost
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectUnitCostPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val repository: ProgrammeUnitCostRepository,
) : ProjectUnitCostPersistence {

    @Transactional(readOnly = true)
    override fun getAvailableUnitCostsForProjectId(projectId: Long, version: String?) =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                repository.findAllForProjectIdOrCall(projectId).toProgrammeUnitCost()
            },
            previousVersionFetcher = { timestamp ->
                repository.findAllForProjectIdOrCallAsOfTimestamp(projectId, timestamp).toEntities().toProgrammeUnitCost()
            }
        )!!.sortedWith(compareBy({ it.projectId == null }, { it.id }))

    @Transactional(readOnly = true)
    override fun getProjectUnitCost(projectId: Long, unitCostId: Long, version: String?) =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                repository.findByIdAndProjectId(id = unitCostId, projectId = projectId).toProgrammeUnitCost()
            },
            previousVersionFetcher = { timestamp ->
                repository.findByIdAndProjectIdAsOfTimestamp(id = unitCostId, projectId = projectId, timestamp)
                    .toEntities().firstOrNull()!!.toProgrammeUnitCost()
            }
        )!!

    @Transactional(readOnly = true)
    override fun existProjectUnitCost(projectId: Long, unitCostId: Long) =
        repository.existsByIdAndProjectId(id = unitCostId, projectId = projectId)

    @Transactional(readOnly = true)
    override fun getProjectUnitCostList(projectId: Long, version: String?) =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                repository.findAllByProjectId(projectId).toProgrammeUnitCost()
            },
            previousVersionFetcher = { timestamp ->
                repository.findAllByProjectIdAsOfTimestamp(projectId, timestamp).toEntities().toProgrammeUnitCost()
            }
        )!!.sortedWith(compareBy({ it.projectId == null }, { it.id }))

    @Transactional(readOnly = true)
    override fun getCount(projectId: Long) =
        repository.countAllByProjectId(projectId)

    @Transactional
    override fun deleteProjectUnitCost(projectId: Long, unitCostId: Long) {
        repository.deleteByIdAndProjectId(unitCostId, projectId = projectId)
    }

    private fun Iterable<ProgrammeUnitCostRow>.toEntities() = groupBy { it.id }
        .map { (unitCostId, rows) ->
            ProgrammeUnitCostEntity(
                id = unitCostId,
                projectId = rows.first().projectId,
                isOneCostCategory = rows.first().oneCostCategory,
                costPerUnit = rows.first().costPerUnit,
                costPerUnitForeignCurrency = rows.first().costPerUnitForeignCurrency,
                foreignCurrencyCode = rows.first().foreignCurrencyCode,
                translatedValues = rows.associateBy { it.language }.mapTo(HashSet()) { (language, translation) ->
                    ProgrammeUnitCostTranslEntity(
                        ProgrammeUnitCostTranslId(unitCostId, language),
                        name = translation.name,
                        description = translation.description,
                        type = translation.type,
                        justification = translation.justification,
                    )
                },
                categories = rows.mapTo(HashSet()) { ProgrammeUnitCostBudgetCategoryEntity(0L, unitCostId, it.category) },
            )
        }

}
