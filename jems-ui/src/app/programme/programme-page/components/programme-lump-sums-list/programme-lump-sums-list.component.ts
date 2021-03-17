import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {ProgrammeLumpSumListDTO} from '@cat/api';
import {UntilDestroy} from '@ngneat/until-destroy';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'app-programme-lump-sums-list',
  templateUrl: './programme-lump-sums-list.component.html',
  styleUrls: ['./programme-lump-sums-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProgrammeLumpSumsListComponent {

  Alert = Alert;

  displayedColumns: string[] = ['name', 'cost'];

  @Input()
  lumpSum: string;

  @Input()
  dataSource: MatTableDataSource<ProgrammeLumpSumListDTO>;

}
