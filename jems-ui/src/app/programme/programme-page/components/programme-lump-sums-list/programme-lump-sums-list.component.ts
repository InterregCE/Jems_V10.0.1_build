import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {InputTranslation, ProgrammeLumpSumListDTO} from '@cat/api';
import {UntilDestroy} from '@ngneat/until-destroy';
import {MatTableDataSource} from '@angular/material/table';
import {LanguageService} from '../../../../common/services/language.service';
import {tap} from 'rxjs/operators';

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
