import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {ProgrammeUnitCostListDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'app-programme-unit-costs-list',
  templateUrl: './programme-unit-costs-list.component.html',
  styleUrls: ['./programme-unit-costs-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProgrammeUnitCostsListComponent {
  Alert = Alert;

  displayedColumns: string[] = ['name', 'type', 'costPerUnit'];

  @Input()
  unitCost: string;
  @Input()
  dataSource: MatTableDataSource<ProgrammeUnitCostListDTO>;

}
