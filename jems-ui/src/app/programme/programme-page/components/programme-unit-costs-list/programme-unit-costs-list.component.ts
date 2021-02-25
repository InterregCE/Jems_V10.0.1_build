import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {InputTranslation, ProgrammeUnitCostListDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {LanguageService} from '../../../../common/services/language.service';

@Component({
  selector: 'app-programme-unit-costs-list',
  templateUrl: './programme-unit-costs-list.component.html',
  styleUrls: ['./programme-unit-costs-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProgrammeUnitCostsListComponent {
  Alert = Alert;

  displayedColumns: string[] = ['name', 'type', 'category', 'costPerUnit'];

  @Input()
  unitCost: string;
  @Input()
  dataSource: MatTableDataSource<ProgrammeUnitCostListDTO>;

  constructor(public languageService: LanguageService) {
  }

  translated(element: InputTranslation[], currentSystemLanguage: string | null): string {
    if (!currentSystemLanguage) {
      return '';
    }
    const elementInSystemLang = element.find((it: InputTranslation) => it.language === currentSystemLanguage);
    return !!elementInSystemLang ? elementInSystemLang.translation : '';
  }
}
