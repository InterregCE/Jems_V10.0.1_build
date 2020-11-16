import {ProjectPartnerBudget} from '../containers/project-application-form-page/services/project-partner-budget.service';
import {InputTranslation, OutputProgrammeLanguage} from '@cat/api';
import {LanguageService} from '../../../common/services/language.service';
import {PartnerBudgetTableEntry} from './partner-budget-table-entry';

export class PartnerBudgetStaffCostTableEntry implements PartnerBudgetTableEntry {
  id?: number;
  description: InputTranslation[] = [];
  numberOfUnits?: number;
  pricePerUnit?: number;
  total?: number;
  new?: boolean;
  availableLanguages: OutputProgrammeLanguage.CodeEnum[];

  validNumberOfUnits = true;
  validPricePerUnit = true;
  validDescription = true;
  validTotal = true;

  constructor(data: Partial<PartnerBudgetStaffCostTableEntry>,
              public languageService: LanguageService) {
    this.languageService.inputLanguageList$
      .pipe(
        // takeUntil(this.destroyed$)
      )
      .subscribe(
        languages => {
          this.availableLanguages = languages;
        });
    this.id = data.id;
    this.description = this.buildMultiLanguageFieldValues(data.description);
    this.numberOfUnits = data.numberOfUnits;
    this.pricePerUnit = data.pricePerUnit;
    this.new = data.new;
    this.total = ProjectPartnerBudget.computeTotal(this.numberOfUnits, this.pricePerUnit);
  }


  private buildMultiLanguageFieldValues(values?: InputTranslation[]): InputTranslation[] {
    let result: InputTranslation[] = [];
    if (values) {
      result = values.map(value => ({language: value.language, translation: value.translation} as InputTranslation));
    }
    this.availableLanguages.forEach(language => {
      if (!result.find(value => value.language === language)) {
        result.push({language, translation: ''});
      }
    });
    return result;
  }

  setNumberOfUnits(newValue: string): void {
    const newNumberOfUnits = ProjectPartnerBudget.toNumber(newValue);
    this.validNumberOfUnits = ProjectPartnerBudget.validNumber(newNumberOfUnits);
    this.numberOfUnits = isNaN(newNumberOfUnits as any) ? NaN : newNumberOfUnits;
    this.computeTotal();
  }

  setPricePerUnit(newValue: string): void {
    const newPricePerUnit = ProjectPartnerBudget.toNumber(newValue);
    this.validPricePerUnit = ProjectPartnerBudget.validNumber(newPricePerUnit);
    this.pricePerUnit = isNaN(newPricePerUnit as any) ? NaN : newPricePerUnit;
    this.computeTotal();
  }

  setDescription(newValue: string): void {
    this.validDescription = !newValue || newValue.length < 250;
    this.description[this.description.findIndex(lang => lang.language === InputTranslation.LanguageEnum.EN)]
      .translation = newValue;
  }

  computeTotal(): void {
    this.total = ProjectPartnerBudget.computeTotal(this.numberOfUnits, this.pricePerUnit);
    this.validTotal = ProjectPartnerBudget.validNumber(this.total);
  }

  valid(): boolean {
    return this.validDescription && this.validNumberOfUnits && this.validPricePerUnit && this.validTotal || false;
  }

}
