import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {PageOutputIndicatorOutput} from '@cat/api';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'app-programme-output-indicators-list',
  templateUrl: './programme-output-indicators-list.component.html',
  styleUrls: ['./programme-output-indicators-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeOutputIndicatorsListComponent extends BaseComponent {

  Alert = Alert;

  @Input()
  indicator: string;
  @Input()
  indicatorPage: PageOutputIndicatorOutput;
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
        elementProperty: 'name',
        sortProperty: 'name'
      },
      {
        displayedColumn: 'output.indicator.table.column.name.priority',
        elementProperty: 'programmePriorityCode',
        sortProperty: 'programmePriorityPolicy.programmePriority.code',
      },
      {
        displayedColumn: 'output.indicator.table.column.name.specific.objective',
        elementProperty: 'programmePriorityPolicyCode',
        sortProperty: 'programmePriorityPolicy.code',
      },
      {
        displayedColumn: 'output.indicator.table.column.name.measurement.unit',
        elementProperty: 'measurementUnit',
        sortProperty: 'measurementUnit'
      },
      {
        displayedColumn: 'output.indicator.table.column.name.milestone',
        elementProperty: 'milestone',
        sortProperty: 'milestone'
      },
      {
        displayedColumn: 'output.indicator.table.column.name.final.target',
        elementProperty: 'finalTarget',
        sortProperty: 'finalTarget'
      }
    ]
  });
}
