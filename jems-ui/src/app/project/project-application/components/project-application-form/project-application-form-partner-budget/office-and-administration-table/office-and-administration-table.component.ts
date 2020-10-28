import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ColDef, GridOptions} from 'ag-grid-community';
import {Numbers} from '../../../../../../common/utils/numbers';
import {TranslateService} from '@ngx-translate/core';

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
      valueGetter: (params: any) => Numbers.toLocale(
        Numbers.truncateNumber(this.total),
        this.locale
      ),
    }
  ];
  locale = 'de-DE';

  gridOptions: GridOptions = {
    domLayout: 'autoHeight',
    defaultColDef: {
      resizable: true,
    },
  }

  constructor(private translateService: TranslateService) {
  }
}
