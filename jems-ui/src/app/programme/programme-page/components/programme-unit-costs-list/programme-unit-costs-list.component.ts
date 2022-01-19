import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {UntilDestroy} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {ProgrammeUnitCostListDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'jems-programme-unit-costs-list',
  templateUrl: './programme-unit-costs-list.component.html',
  styleUrls: ['./programme-unit-costs-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProgrammeUnitCostsListComponent {
  Alert = Alert;

  displayedColumns: string[] = ['name', 'type', 'category', 'costPerUnit'];

  @Input()
  unitCost: string;
  @Input()
  dataSource: MatTableDataSource<ProgrammeUnitCostListDTO>;
}
