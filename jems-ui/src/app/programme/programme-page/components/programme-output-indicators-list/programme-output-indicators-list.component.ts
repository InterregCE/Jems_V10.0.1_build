import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {OutputIndicatorDetailDTO, PageOutputIndicatorDetailDTO, ProgrammeIndicatorOutputService, UserRoleCreateDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {catchError, filter, switchMap, take, tap} from 'rxjs/operators';
import {Subject} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {HttpErrorResponse} from '@angular/common/http';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;


@Component({
  selector: 'jems-programme-output-indicators-list',
  templateUrl: './programme-output-indicators-list.component.html',
  styleUrls: ['./programme-output-indicators-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeOutputIndicatorsListComponent extends BaseComponent implements OnInit {

  Alert = Alert;

  @Input()
  indicatorPage: PageOutputIndicatorDetailDTO;
  @Input()
  pageIndex: number;
  @Input()
  isProgrammeSetupRestricted: boolean;

  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();
  @Output()
  deleted: EventEmitter<void> = new EventEmitter<void>();

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;
  @ViewChild('resultIndicator', {static: true})
  resultIndicator: TemplateRef<any>;

  indicatorTableConfiguration: TableConfiguration;

  outputIndicatorDeleteError$ = new Subject<APIError | null>();
  outputIndicatorDeleteSuccess$ = new Subject<boolean>();

  constructor(private permissionService: PermissionService,
              private programmeIndicatorService: ProgrammeIndicatorOutputService,
              private dialog: MatDialog) {

    super();
  }

  ngOnInit(): void{
    this.indicatorTableConfiguration = new TableConfiguration({
      routerLink: '/app/programme/indicators/outputIndicator/detail',
      isTableClickable: true,
      columns: [
        {
          displayedColumn: 'output.indicator.table.column.name.identifier',
          elementProperty: 'identifier',
          sortProperty: 'identifier'
        },
        {
          displayedColumn: 'output.indicator.table.column.name.code',
          elementProperty: 'code',
          sortProperty: 'code',
        },
        {
          displayedColumn: 'output.indicator.table.column.name.name',
          columnType: ColumnType.InputTranslation,
          elementProperty: 'name',
        },
        {
          displayedColumn: 'output.indicator.table.column.name.priority',
          elementProperty: 'programmePriorityCode',
          sortProperty: 'programmePriorityPolicyEntity.programmePriority.code',
        },
        {
          displayedColumn: 'output.indicator.table.column.name.specific.objective',
          elementProperty: 'programmePriorityPolicyCode',
          sortProperty: 'programmePriorityPolicyEntity.code',
        },
        {
          displayedColumn: 'output.indicator.table.column.name.measurement.unit',
          columnType: ColumnType.InputTranslation,
          elementProperty: 'measurementUnit',
        },
        {
          displayedColumn: 'output.indicator.table.column.name.result.indicator',
          customCellTemplate: this.resultIndicator
        },
        {
          displayedColumn: 'output.indicator.table.column.name.milestone',
          elementProperty: 'milestone',
          sortProperty: 'milestone',
          columnType: ColumnType.Decimal
        },
        {
          displayedColumn: 'output.indicator.table.column.name.final.target',
          elementProperty: 'finalTarget',
          sortProperty: 'finalTarget',
          columnType: ColumnType.Decimal
        },
        ...this.permissionService.hasPermission(PermissionsEnum.ProgrammeSetupUpdate) ? [{
          displayedColumn: 'common.delete.entry',
          customCellTemplate: this.deleteCell,
          columnWidth: ColumnWidth.IdColumn
        }] : []
      ]
    });
  }
  delete(outputIndicator: OutputIndicatorDetailDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'output.indicator.final.dialog.title.delete',
        message: {i18nKey: 'output.indicator.final.dialog.message.delete', i18nArguments: {name: outputIndicator.identifier}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.programmeIndicatorService.deleteOutputIndicator(outputIndicator.id)),
        tap(() => this.outputIndicatorDeleteError$.next(null)),
        tap(() => this.outputIndicatorDeleteSuccess$.next(true)),
        tap(() => setTimeout(() => this.outputIndicatorDeleteSuccess$.next(false), 3000)),
        catchError((error: HttpErrorResponse) => {
          this.outputIndicatorDeleteError$.next(error.error);
          this.outputIndicatorDeleteSuccess$.next(false);
          throw error;
        }),
        tap(() => this.deleted.emit())
      ).subscribe();
  }
}
