import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {Alert} from '@common/components/forms/alert';
import {PageProgrammeLumpSumDTO} from '@cat/api';
import {UntilDestroy} from '@ngneat/until-destroy';
import {ColumnType} from '@common/components/table/model/column-type.enum';

@Component({
  selector: 'app-programme-lump-sums-list',
  templateUrl: './programme-lump-sums-list.component.html',
  styleUrls: ['./programme-lump-sums-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProgrammeLumpSumsListComponent {

  Alert = Alert;

  @Input()
  lumpSum: string;
  @Input()
  lumpSumPage: PageProgrammeLumpSumDTO;
  @Input()
  pageIndex: number;


  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  lumpSumTableConfiguration = new TableConfiguration({
    routerLink: '/app/programme/costs/lumpSum/detail',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'lump.sum.table.column.name.name',
        elementProperty: 'name',
        sortProperty: 'name'
      },
      {
        displayedColumn: 'lump.sum.table.column.name.cost',
        elementProperty: 'cost',
        sortProperty: 'cost',
        columnType: ColumnType.Decimal
      }
    ]
  });
}
