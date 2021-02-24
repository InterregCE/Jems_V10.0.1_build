import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {InputTranslation, ProgrammeUnitCostListDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {LanguageService} from '../../../../common/services/language.service';
import {tap} from 'rxjs/operators';

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

  currentSystemLanguage: string;

  constructor(private languageService: LanguageService) {
    this.languageService.systemLanguage$
      .pipe(
        tap(lang => {
          this.currentSystemLanguage = lang;
        })
      ).subscribe();
  }

  translated(element: InputTranslation[]): string {
    const elementInSystemLang = element.find((it: InputTranslation) => it.language === this.currentSystemLanguage);
    return !!elementInSystemLang ? elementInSystemLang.translation : '';
  }
}
