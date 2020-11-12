import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {PageOutputIndicatorResult} from '@cat/api';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'app-programme-result-indicators-list',
  templateUrl: './programme-result-indicators-list.component.html',
  styleUrls: ['./programme-result-indicators-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeResultIndicatorsListComponent extends BaseComponent {

  Alert = Alert;

  @Input()
  indicator: string;
  @Input()
  indicatorPage: PageOutputIndicatorResult;
  @Input()
  pageIndex: number;


  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  indicatorTableConfiguration = new TableConfiguration({
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
        elementProperty: 'name',
        sortProperty: 'name'
      },
      {
        displayedColumn: 'result.indicator.table.column.name.priority',
        elementProperty: 'programmePriorityCode',
        sortProperty: 'programmePriorityPolicy.programmePriority.code',
      },
      {
        displayedColumn: 'result.indicator.table.column.name.specific.objective',
        elementProperty: 'programmePriorityPolicyCode',
        sortProperty: 'programmePriorityPolicy.code',
      },
      {
        displayedColumn: 'result.indicator.table.column.name.measurement.unit',
        elementProperty: 'measurementUnit',
        sortProperty: 'measurementUnit'
      },
      {
        displayedColumn: 'result.indicator.table.column.name.baseline',
        elementProperty: 'baseline',
        sortProperty: 'baseline'
      },
      {
        displayedColumn: 'result.indicator.table.column.name.reference.year',
        elementProperty: 'referenceYear',
        sortProperty: 'referenceYear'
      },
      {
        displayedColumn: 'result.indicator.table.column.name.final.target',
        elementProperty: 'finalTarget',
        sortProperty: 'finalTarget'
      }
    ]
  });
}
