import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ProgrammePageSidenavService} from '../../programme-page/services/programme-page-sidenav.service';
import {ProgrammePrioritiesPageStore} from './programme-priorities-page-store.service';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ProgrammePriorityDTO, UserRoleDTO} from '@cat/api';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {ColumnWidth} from '@common/components/table/model/column-width';
import {catchError, filter, switchMap, take, tap} from 'rxjs/operators';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {ProgrammeEditableStateStore} from '../../programme-page/services/programme-editable-state-store.service';
import {PermissionService} from '../../../security/permissions/permission.service';
import {HttpErrorResponse} from '@angular/common/http';
import {Subject} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-programme-priority-list-page',
  templateUrl: './programme-priority-list-page.component.html',
  styleUrls: ['./programme-priority-list-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ProgrammePrioritiesPageStore]
})
export class ProgrammePriorityListPageComponent implements OnInit {
  Alert = Alert;

  PermissionsEnum = PermissionsEnum;

  @ViewChild('specificObjective', {static: true})
  specificObjective: TemplateRef<any>;

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;

  tableConfiguration: TableConfiguration;
  isProgrammeSetupRestricted: boolean;

  priorityDeleteError$ = new Subject<APIError | null>();
  priorityDeleteSuccess$ = new Subject<boolean>();

  constructor(private programmePageSidenavService: ProgrammePageSidenavService,
              public prioritiesPageStore: ProgrammePrioritiesPageStore,
              public programmeEditableStateStore: ProgrammeEditableStateStore,
              private dialog: MatDialog,
              private permissionService: PermissionService) {
    this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$.pipe(
      tap(isProgrammeEditingLimited => this.isProgrammeSetupRestricted = isProgrammeEditingLimited),
    ).subscribe();
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      routerLink: '/app/programme/priorities',
      isTableClickable: true,
      sortable: false,
      columns: [
        {
          displayedColumn: 'programme.priority.list.column.code',
          elementProperty: 'code'
        },
        {
          displayedColumn: 'programme.priority.list.column.title',
          elementProperty: 'title',
          columnType: ColumnType.InputTranslation
        },
        {
          displayedColumn: 'programme.priority.list.column.objective',
          customCellTemplate: this.specificObjective
        },
        ...this.permissionService.hasPermission(PermissionsEnum.ProgrammeSetupUpdate) ? [{
          displayedColumn: 'common.delete.entry',
          customCellTemplate: this.deleteCell,
          columnWidth: ColumnWidth.IdColumn
        }] : []
      ]
    });
  }

  delete(priority: ProgrammePriorityDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'programme.priority.dialog.title.delete',
        message: {i18nKey: 'programme.priority.dialog.message.delete', i18nArguments: {name: priority.code}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.prioritiesPageStore.deletePriority(priority.id)),
        tap(() => this.priorityDeleteError$.next(null)),
        tap(() => this.priorityDeleteSuccess$.next(true)),
        tap(() => setTimeout(() => this.priorityDeleteSuccess$.next(false), 3000)),
        catchError((error: HttpErrorResponse) => {
          this.priorityDeleteError$.next(error.error);
          this.priorityDeleteSuccess$.next(false);
          throw error;
        })
      ).subscribe();
  }
}
