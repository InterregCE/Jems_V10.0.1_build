import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ColDef, GridOptions} from 'ag-grid-community';
import {TranslateService} from '@ngx-translate/core';
import {NumberService} from '../../../../../../common/services/number.service';

@Component({
  selector: 'app-office-and-administration-table',
  templateUrl: './office-and-administration-table.component.html',
  styleUrls: ['./office-and-administration-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OfficeAndAdministrationTableComponent {

  @Input()
  total: number;

  columnDefs: Partial<ColDef>[] = [
    {
      headerName: this.translateService.instant('project.partner.budget.office.and.admin.flat.rate'),
      cellRenderer: () => this.translateService.instant('project.partner.budget.table.total'),
      flex: 1,
    },
    {
      headerName: this.translateService.instant('project.partner.budget.table.total'),
      field: 'total',
      type: 'numericColumn',
      valueGetter: (params: any) => NumberService.toLocale(NumberService.truncateNumber(this.total)),
      cellStyle: {'text-align': 'right'}
    }
  ];

  gridOptions: GridOptions = {
    domLayout: 'autoHeight',
    defaultColDef: {
      resizable: true,
    },
  };

  constructor(private translateService: TranslateService) {
  }
}
