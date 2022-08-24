import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {PageOutputIndicatorDetailDTO} from '@cat/api';
import {Alert} from '@common/components/forms/alert';
import {ColumnType} from '@common/components/table/model/column-type.enum';

@Component({
  selector: 'jems-programme-output-indicators-list',
  templateUrl: './programme-output-indicators-list.component.html',
  styleUrls: ['./programme-output-indicators-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeOutputIndicatorsListComponent extends BaseComponent {

  Alert = Alert;

  @Input()
  indicator: string;
  @Input()
  indicatorPage: PageOutputIndicatorDetailDTO;
  @Input()
  pageIndex: number;


  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  indicatorTableConfiguration = new TableConfiguration({
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
      }
    ]
  });
}
