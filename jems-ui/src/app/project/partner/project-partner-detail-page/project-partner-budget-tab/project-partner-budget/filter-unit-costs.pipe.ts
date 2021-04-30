import {Pipe, PipeTransform} from '@angular/core';
import {ProgrammeUnitCost} from '../../../../model/programmeUnitCost';
import {BudgetCostCategoryEnum} from '../../../../model/lump-sums/BudgetCostCategoryEnum';

@Pipe({
  name: 'filterUnitCosts',
  pure: true
})
export class FilterUnitCostsPipe implements PipeTransform {

  transform(value: ProgrammeUnitCost[], isOneCostCategory = true, category: BudgetCostCategoryEnum | null = null): ProgrammeUnitCost[] {
    let result = value.filter(it => it.isOneCostCategory === isOneCostCategory);
    if (category && isOneCostCategory) {
      result = result.filter(it => it.categories.length === 1 && it.categories[0] === category);
    }
    return result;
  }

}
