import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {InputTranslation, ProgrammeLumpSumListDTO} from '@cat/api';
import {UntilDestroy} from '@ngneat/until-destroy';
import {MatTableDataSource} from '@angular/material/table';
import {LanguageService} from '../../../../common/services/language.service';

@Component({
  selector: 'app-programme-lump-sums-list',
  templateUrl: './programme-lump-sums-list.component.html',
  styleUrls: ['./programme-lump-sums-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProgrammeLumpSumsListComponent {

  Alert = Alert;

  displayedColumns: string[] = ['name', 'cost'];

  @Input()
  lumpSum: string;

  @Input()
  dataSource: MatTableDataSource<ProgrammeLumpSumListDTO>;

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
