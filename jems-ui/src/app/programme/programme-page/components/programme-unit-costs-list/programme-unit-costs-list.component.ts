import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import { Alert } from '@common/components/forms/alert';
import {PageProgrammeUnitCostDTO} from '@cat/api';

@Component({
  selector: 'app-programme-unit-costs-list',
  templateUrl: './programme-unit-costs-list.component.html',
  styleUrls: ['./programme-unit-costs-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProgrammeUnitCostsListComponent {
  Alert = Alert;

  @Input()
  unitCost: string;
  @Input()
  unitCostPage: PageProgrammeUnitCostDTO;
  @Input()
  pageIndex: number;


  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  unitCostTableConfiguration = new TableConfiguration({
    routerLink: '/app/programme/costs/unitCost/detail',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'unit.cost.table.column.name.name',
        elementProperty: 'name',
        sortProperty: 'name'
      },
      {
        displayedColumn: 'unit.cost.table.column.name.unit.type',
        elementProperty: 'type',
        sortProperty: 'type',
      },
      {
        displayedColumn: 'unit.cost.table.column.name.cost.unit',
        elementProperty: 'costPerUnit',
        sortProperty: 'costPerUnit',
      }
    ]
  });
}
