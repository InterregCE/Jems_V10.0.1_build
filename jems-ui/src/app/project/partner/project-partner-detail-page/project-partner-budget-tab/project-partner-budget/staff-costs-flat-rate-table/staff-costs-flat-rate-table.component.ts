import {Component, Input} from '@angular/core';
import {ColDef, GridOptions} from 'ag-grid-community';
import {Numbers} from '../../../../../../common/utils/numbers';
import {TranslateService} from '@ngx-translate/core';

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
      valueGetter: (params: any) => Numbers.toLocale(
        Numbers.truncateNumber(params.data.total),
        this.locale
      ),
      cellStyle: { 'text-align': 'right' }
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
