package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputGeneralBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputStaffCostBudget
import io.cloudflight.jems.api.project.dto.partner.budget.InputTravelBudget
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.budget.Budget
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEquipment
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEquipmentTransl
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetExternal
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetExternalTransl
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructure
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructureTransl
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCost
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCostTransl
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravel
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravelTransl
import java.math.BigDecimal

fun InputBudget.getNumberOfUnits(): BigDecimal = numberOfUnits.truncate()
fun InputBudget.getPricePerUnit(): BigDecimal = pricePerUnit.truncate()

/**
 * Generalized:
 *  - budget_equipment
 *  - budget_external
 *  - budget_infrastructure
 * Specific:
 *  - budget_staff_cost
 *  - budget_travel
 */
private fun toBudget(
    numberOfUnits: BigDecimal,
    pricePerUnit: BigDecimal
): Budget = Budget(
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = numberOfUnits.multiply(pricePerUnit).truncate()
)

fun InputTravelBudget.toTravel(partnerId: Long) = ProjectPartnerBudgetTravel(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    ),
    translatedValues = combineTranslatedValuesTravelCost(partnerId, description)
)

fun combineTranslatedValuesTravelCost(
    partnerId: Long,
    description: Set<InputTranslation>
): Set<ProjectPartnerBudgetTravelTransl> {
    val descriptionMap = description.associateBy( { it.language }, { it.translation } )

    val languages = descriptionMap.keys.toMutableSet()
    languages.addAll(descriptionMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectPartnerBudgetTravelTransl(
            TranslationPartnerId(partnerId, it),
            descriptionMap[it]
        )
    }
}

fun InputStaffCostBudget.toStaffCost(partnerId: Long) = ProjectPartnerBudgetStaffCost(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    ),
    translatedValues = combineTranslatedValuesStaffCost(partnerId, description)
)

fun combineTranslatedValuesStaffCost(
    partnerId: Long,
    description: Set<InputTranslation>
): Set<ProjectPartnerBudgetStaffCostTransl> {
    val descriptionMap = description.associateBy( { it.language }, { it.translation } )

    val languages = descriptionMap.keys.toMutableSet()
    languages.addAll(descriptionMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectPartnerBudgetStaffCostTransl(
            TranslationPartnerId(partnerId, it),
            descriptionMap[it]
        )
    }
}

fun InputGeneralBudget.toEquipment(partnerId: Long) = ProjectPartnerBudgetEquipment(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    ),
    translatedValues = combineTranslatedValuesEquipment(partnerId, description)
)

fun combineTranslatedValuesEquipment(
    partnerId: Long,
    description: Set<InputTranslation>
): Set<ProjectPartnerBudgetEquipmentTransl> {
    val descriptionMap = description.associateBy( { it.language }, { it.translation } )

    val languages = descriptionMap.keys.toMutableSet()
    languages.addAll(descriptionMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectPartnerBudgetEquipmentTransl(
            TranslationPartnerId(partnerId, it),
            descriptionMap[it]
        )
    }
}

fun InputGeneralBudget.toExternal(partnerId: Long) = ProjectPartnerBudgetExternal(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    ),
    translatedValues = combineTranslatedValuesExternal(partnerId, description)
)

fun combineTranslatedValuesExternal(
    partnerId: Long,
    description: Set<InputTranslation>
): Set<ProjectPartnerBudgetExternalTransl> {
    val descriptionMap = description.associateBy( { it.language }, { it.translation } )

    val languages = descriptionMap.keys.toMutableSet()
    languages.addAll(descriptionMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectPartnerBudgetExternalTransl(
            TranslationPartnerId(partnerId, it),
            descriptionMap[it]
        )
    }
}

fun InputGeneralBudget.toInfrastructure(partnerId: Long) = ProjectPartnerBudgetInfrastructure(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    ),
    translatedValues = combineTranslatedValuesInfrastructure(partnerId, description)
)

fun combineTranslatedValuesInfrastructure(
    partnerId: Long,
    description: Set<InputTranslation>
): Set<ProjectPartnerBudgetInfrastructureTransl> {
    val descriptionMap = description.associateBy( { it.language }, { it.translation } )

    val languages = descriptionMap.keys.toMutableSet()
    languages.addAll(descriptionMap.keys)

    return languages.mapTo(HashSet()) {
        ProjectPartnerBudgetInfrastructureTransl(
            TranslationPartnerId(partnerId, it),
            descriptionMap[it]
        )
    }
}

/* output mappings */

fun ProjectPartnerBudgetStaffCost.toStaffCostOutput() = InputStaffCostBudget(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.description)
    },
    numberOfUnits = budget.numberOfUnits,
    pricePerUnit = budget.pricePerUnit,
    rowSum = budget.rowSum
)

fun ProjectPartnerBudgetTravel.toTravelOutput() = InputTravelBudget(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.description)
    },
    numberOfUnits = budget.numberOfUnits,
    pricePerUnit = budget.pricePerUnit,
    rowSum = budget.rowSum
)

fun ProjectPartnerBudgetEquipment.toEquipmentOutput() = InputGeneralBudget(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.description)
    },
    numberOfUnits = budget.numberOfUnits,
    pricePerUnit = budget.pricePerUnit,
    rowSum = budget.rowSum
)

fun ProjectPartnerBudgetExternal.toExternalOutput() = InputGeneralBudget(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.description)
    },
    numberOfUnits = budget.numberOfUnits,
    pricePerUnit = budget.pricePerUnit,
    rowSum = budget.rowSum
)

fun ProjectPartnerBudgetInfrastructure.toInfrastructureOutput() = InputGeneralBudget(
    id = id,
    description = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.translationId.language, it.description)
    },
    numberOfUnits = budget.numberOfUnits,
    pricePerUnit = budget.pricePerUnit,
    rowSum = budget.rowSum
)
