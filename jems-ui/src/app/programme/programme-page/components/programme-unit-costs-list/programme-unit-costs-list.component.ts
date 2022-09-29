import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {ProgrammeCostOptionService, ProgrammeUnitCostListDTO, UserRoleCreateDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {Observable, Subject} from 'rxjs';
import {PermissionService} from '../../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {MatDialog} from '@angular/material/dialog';
import {LanguageStore} from '@common/services/language-store.service';
import {
  MultiLanguageGlobalService
} from '@common/components/forms/multi-language-container/multi-language-global.service';
import {catchError, filter, switchMap, take, tap} from 'rxjs/operators';
import {Forms} from '@common/utils/forms';
import {APIError} from '@common/models/APIError';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'jems-programme-unit-costs-list',
  templateUrl: './programme-unit-costs-list.component.html',
  styleUrls: ['./programme-unit-costs-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProgrammeUnitCostsListComponent {
  Alert = Alert;
  PermissionsEnum = PermissionsEnum;

  displayedColumns: string[] = ['name', 'type', 'category', 'costPerUnit','delete'];

  @Input()
  dataSource: MatTableDataSource<ProgrammeUnitCostListDTO>;

  @Output()
  deleted: EventEmitter<void> = new EventEmitter<void>();



  isProgrammeSetupRestricted(): Observable<boolean> {
    return this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$;
  }

  unitCostDeleteError$ = new Subject<APIError | null>();
  unitCostDeleteSuccess$ = new Subject<boolean>();

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

  deleteUnitCost(unitCost: ProgrammeUnitCostListDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'unit.cost.final.dialog.title.delete',
        message: {i18nKey: 'unit.cost.final.dialog.message.delete', i18nArguments: {name: unitCost.name.find(it => it.language === this.currentLanguage)?.translation || ''}},
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.programmeCostOptionService.deleteProgrammeUnitCost(unitCost.id)),
        tap(() => this.unitCostDeleteError$.next(null)),
        tap(() => this.unitCostDeleteSuccess$.next(true)),
        tap(() => setTimeout(() => this.unitCostDeleteSuccess$.next(false), 3000)),
        catchError((error: HttpErrorResponse) => {
          this.unitCostDeleteError$.next(error.error);
          this.unitCostDeleteSuccess$.next(false);
          throw error;
        }),
        tap(() => this.deleted.emit()),
      ).subscribe();
  }
}
