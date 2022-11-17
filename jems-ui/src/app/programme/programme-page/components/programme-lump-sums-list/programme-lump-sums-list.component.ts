import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {ProgrammeCostOptionService, ProgrammeLumpSumListDTO, UserRoleCreateDTO} from '@cat/api';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {MatTableDataSource} from '@angular/material/table';
import {PermissionService} from '../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {Observable, Subject} from 'rxjs';
import {catchError, filter, switchMap, take, tap} from 'rxjs/operators';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {LanguageStore} from '@common/services/language-store.service';
import {
  MultiLanguageGlobalService
} from '@common/components/forms/multi-language-container/multi-language-global.service';
import {APIError} from '@common/models/APIError';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'jems-programme-lump-sums-list',
  templateUrl: './programme-lump-sums-list.component.html',
  styleUrls: ['./programme-lump-sums-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProgrammeLumpSumsListComponent {

  Alert = Alert;
  PermissionsEnum = PermissionsEnum;

  displayedColumns: string[] = ['name', 'cost', 'delete'];

  @Input()
  dataSource: MatTableDataSource<ProgrammeLumpSumListDTO>;

  @Output()
  deleted: EventEmitter<void> = new EventEmitter<void>();

  lumpSumDeleteError$ = new Subject<APIError | null>();
  lumpSumDeleteSuccess$ = new Subject<boolean>();

  isProgrammeSetupRestricted(): Observable<boolean> {
    return this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$;
  }

  currentLanguage: string;

  constructor(public permissionService: PermissionService,
              private programmeEditableStateStore: ProgrammeEditableStateStore,
              private dialog: MatDialog,
              private programmeCostOptionService: ProgrammeCostOptionService,
              private languageStore: LanguageStore,
              private multiLanguageGlobalService: MultiLanguageGlobalService) {
    this.multiLanguageGlobalService.activeInputLanguage$.pipe(
      tap(language => this.currentLanguage = language),
      untilDestroyed(this),
    ).subscribe();
  }

  deleteLumpSum(lumpSum: ProgrammeLumpSumListDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'lump.sum.final.dialog.title.delete',
        message: {i18nKey: 'lump.sum.final.dialog.message.delete', i18nArguments: {name: lumpSum.name.find(it => it.language === this.currentLanguage)?.translation || ''}},
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.programmeCostOptionService.deleteProgrammeLumpSum(lumpSum.id)),
        tap(() => this.lumpSumDeleteError$.next(null)),
        tap(() => this.lumpSumDeleteSuccess$.next(true)),
        tap(() => setTimeout(() => this.lumpSumDeleteSuccess$.next(false), 3000)),
        catchError((error: HttpErrorResponse) => {
          this.lumpSumDeleteError$.next(error.error);
          this.lumpSumDeleteSuccess$.next(false);
          throw error;
        }),
        tap(() => this.deleted.emit()),
      ).subscribe();
  }
}
