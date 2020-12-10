import {Component, Input} from '@angular/core';
import {ColDef, GridOptions} from 'ag-grid-community';
import {TranslateService} from '@ngx-translate/core';
import {NumberService} from '../../../../../../common/services/number.service';

@Component({
  selector: 'app-staff-costs-flat-rate-table',
  templateUrl: './staff-costs-flat-rate-table.component.html',
  styleUrls: ['./staff-costs-flat-rate-table.component.scss']
})
export class StaffCostsFlatRateTableComponent {


  @Input()
  total: { description: string, total: number };

  columnDefs: Partial<ColDef>[] = [
    {
      headerName: this.translateService.instant('project.partner.budget.staff.costs.flat.rate'),
      field: 'description',
      flex: 1,
    },
    {
      headerName: this.translateService.instant('project.partner.budget.table.total'),
      field: 'total',
      type: 'numericColumn',
      valueGetter: (params: any) => NumberService.toLocale(NumberService.truncateNumber(params.data.total)),
      cellStyle: {'text-align': 'right'}
    }
  ];
  locale = 'de-DE';

  gridOptions: GridOptions = {
    domLayout: 'autoHeight',
    defaultColDef: {
      resizable: true,
    },
  };

  constructor(private translateService: TranslateService) {
  }

}
