import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ColDef, GridOptions} from 'ag-grid-community';
import {Numbers} from '../../../../../../common/utils/numbers';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-travel-and-accommodation-flat-rate-table',
  templateUrl: './travel-and-accommodation-flat-rate-table.component.html',
  styleUrls: ['./travel-and-accommodation-flat-rate-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TravelAndAccommodationFlatRateTableComponent {

  @Input()
  total: number;

  columnDefs: Partial<ColDef>[] = [
    {
      headerName: this.translateService.instant('project.partner.budget.travel.and.accommodation.flat.rate.header'),
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
