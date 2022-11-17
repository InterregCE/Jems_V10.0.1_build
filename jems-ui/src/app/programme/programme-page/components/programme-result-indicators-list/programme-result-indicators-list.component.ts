import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output, TemplateRef, ViewChild, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {Alert} from '@common/components/forms/alert';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {
  PageResultIndicatorDetailDTO,
  ProgrammeIndicatorResultService,
  ResultIndicatorDetailDTO, UserRoleCreateDTO
} from '@cat/api';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {Forms} from '@common/utils/forms';
import {catchError, filter, switchMap, take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {HttpErrorResponse} from '@angular/common/http';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {PermissionService} from '../../../../security/permissions/permission.service';
import {Subject} from 'rxjs';
import {APIError} from '@common/models/APIError';

@Component({
  selector: 'jems-programme-result-indicators-list',
  templateUrl: './programme-result-indicators-list.component.html',
  styleUrls: ['./programme-result-indicators-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeResultIndicatorsListComponent extends BaseComponent implements OnInit {

  Alert = Alert;

  @Input()
  indicatorPage: PageResultIndicatorDetailDTO;
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

  indicatorTableConfiguration: TableConfiguration;

  resultIndicatorDeleteError$ = new Subject<APIError | null>();
  resultIndicatorDeleteSuccess$ = new Subject<boolean>();

  constructor(private dialog: MatDialog,
              private programmeIndicatorService: ProgrammeIndicatorResultService,
              private permissionService: PermissionService) {
    super();
  }

  ngOnInit(): void {
    this.indicatorTableConfiguration = new TableConfiguration({
      routerLink: '/app/programme/indicators/resultIndicator/detail',
      isTableClickable: true,
      columns: [
        {
          displayedColumn: 'result.indicator.table.column.name.identifier',
          elementProperty: 'identifier',
          sortProperty: 'identifier'
        },
        {
          displayedColumn: 'result.indicator.table.column.name.code',
          elementProperty: 'code',
          sortProperty: 'code',
        },
        {
          displayedColumn: 'result.indicator.table.column.name.name',
          columnType: ColumnType.InputTranslation,
          elementProperty: 'name',
        },
        {
          displayedColumn: 'result.indicator.table.column.name.priority',
          elementProperty: 'programmePriorityCode',
          sortProperty: 'programmePriorityPolicyEntity.programmePriority.code',
        },
        {
          displayedColumn: 'result.indicator.table.column.name.specific.objective',
          elementProperty: 'programmePriorityPolicyCode',
          sortProperty: 'programmePriorityPolicyEntity.code',
        },
        {
          displayedColumn: 'result.indicator.table.column.name.measurement.unit',
          columnType: ColumnType.InputTranslation,
          elementProperty: 'measurementUnit',
        },
        {
          displayedColumn: 'result.indicator.table.column.name.reference.year',
          elementProperty: 'referenceYear',
          sortProperty: 'referenceYear'
        },
        {
          displayedColumn: 'result.indicator.table.column.name.baseline',
          elementProperty: 'baseline',
          sortProperty: 'baseline',
          columnType: ColumnType.Decimal
        },
        {
          displayedColumn: 'result.indicator.table.column.name.final.target',
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
  delete(resultIndicator: ResultIndicatorDetailDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'result.indicator.final.dialog.title.delete',
        message: {i18nKey: 'result.indicator.final.dialog.message.delete', i18nArguments: {name: resultIndicator.identifier}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.programmeIndicatorService.deleteResultIndicator(resultIndicator.id)),
        tap(() => this.resultIndicatorDeleteError$.next(null)),
        tap(() => this.resultIndicatorDeleteSuccess$.next(true)),
        tap(() => setTimeout(() => this.resultIndicatorDeleteSuccess$.next(false), 3000)),
        catchError((error: HttpErrorResponse) => {
          this.resultIndicatorDeleteError$.next(error.error);
          this.resultIndicatorDeleteSuccess$.next(false);
          throw error;
        }),
        tap(() => this.deleted.emit())
      ).subscribe();
  }
}
