export enum BudgetCostCategoryEnum {
  STAFF_COSTS = 'StaffCosts',
  OFFICE_AND_ADMINISTRATION_COSTS = 'OfficeAndAdministrationCosts',
  TRAVEL_AND_ACCOMMODATION_COSTS = 'TravelAndAccommodationCosts',
  EXTERNAL_COSTS = 'ExternalCosts',
  EQUIPMENT_COSTS = 'EquipmentCosts',
  INFRASTRUCTURE_COSTS = 'InfrastructureCosts'
}

export class BudgetCostCategoryEnumUtils {

  public static toBudgetCostCategoryEnums(categories: string[]): BudgetCostCategoryEnum [] {
    return categories.map(it => BudgetCostCategoryEnumUtils.toBudgetCostCategoryEnum(it)).filter(it => it === null) as BudgetCostCategoryEnum [];
  }

  private static toBudgetCostCategoryEnum(category: string): BudgetCostCategoryEnum | null {
    switch (category) {
      case 'StaffCosts':
        return BudgetCostCategoryEnum.STAFF_COSTS;
      case 'OfficeAndAdministrationCosts':
        return BudgetCostCategoryEnum.OFFICE_AND_ADMINISTRATION_COSTS;
      case 'TravelAndAccommodationCosts':
        return BudgetCostCategoryEnum.TRAVEL_AND_ACCOMMODATION_COSTS;
      case 'ExternalCosts':
        return BudgetCostCategoryEnum.EXTERNAL_COSTS;
      case 'EquipmentCosts':
        return BudgetCostCategoryEnum.EQUIPMENT_COSTS;
      case 'InfrastructureCosts':
        return BudgetCostCategoryEnum.INFRASTRUCTURE_COSTS;
      default:
        return null;
    }
  }
}

