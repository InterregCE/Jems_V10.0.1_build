import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input, Output, TemplateRef,
  ViewChild
} from '@angular/core';
import {Tables} from '../../../../../../common/utils/tables';
import {PartnerBudgetTableEntry} from '../../../../model/partner-budget-table-entry';
import {CellValueChangedEvent, ColDef, GridApi, GridOptions, RowNode} from 'ag-grid-community';
import {AgGridTemplateRendererComponent} from './ag-grid-template-renderer/ag-grid-template-renderer.component';
import {TranslateService} from '@ngx-translate/core';
import {PartnerBudgetTable} from '../../../../model/partner-budget-table';
import {Numbers} from '../../../../../../common/utils/numbers';
import {BaseComponent} from '@common/components/base-component';
import {PartnerBudgetTableType} from '../../../../model/partner-budget-table-type';
import {MultiLanguageInputService} from '../../../../../../common/services/multi-language-input.service';
import {takeUntil, tap} from 'rxjs/operators';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {InputTranslation} from '@cat/api';

@Component({
  selector: 'app-budget-table',
  templateUrl: './budget-table.component.html',
  styleUrls: ['./budget-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BudgetTableComponent extends BaseComponent implements AfterViewInit {
  Numbers = Numbers;

  @Input()
  editable: boolean;
  @Input()
  table: PartnerBudgetTable;

  @Output()
  tableChanged = new EventEmitter<void>();

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;
  @ViewChild('descriptionCell', {static: true})
  descriptionCell: TemplateRef<any>;
  @ViewChild('totalCell', {static: true})
  totalCell: TemplateRef<any>;

  gridApi?: GridApi;
  columnDefs: Partial<ColDef>[] = [];
  locale = 'de-DE';

  gridOptions: GridOptions = {
    stopEditingWhenGridLosesFocus: true,
    domLayout: 'autoHeight',
    defaultColDef: {
      resizable: true,
    },
    onCellValueChanged: (event: CellValueChangedEvent) => {
      this.tableChanged.emit();
      if (event.colDef.field === 'numberOfUnits' || event.colDef.field === 'pricePerUnit') {
        this.gridApi?.setPinnedBottomRowData([this.getTotalRow()]);
      }
    },
  };

  constructor(private translateService: TranslateService,
              public languageService: MultiLanguageInputService) {
    super();
    this.languageService.currentLanguage$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.gridApi?.refreshView())
      )
      .subscribe();
  }

  static editableRow(editable: boolean, node: RowNode): boolean {
    return editable && !node.isRowPinned();
  }

  ngAfterViewInit(): void {
    this.setColumnDefs();
  }

  addNewEntry(): void {
    this.table.entries = [...this.table.entries, new PartnerBudgetTableEntry({
      id: Tables.getNextId(this.table.entries),
      description: this.languageService.initInput([]),
      numberOfUnits: 1,
      pricePerUnit: 0,
      new: true
    })];
    this.tableChanged.emit();
  }

  removeEntry(entry: PartnerBudgetTableEntry): void {
    this.table.entries = this.table.entries.filter(element => element.id !== entry.id);
    this.tableChanged.emit();
    this.gridApi?.setPinnedBottomRowData([this.getTotalRow()]);
  }

  gridReady(): void {
    this.gridApi = this.gridOptions.api as any;
    this.gridApi?.setPinnedBottomRowData([this.getTotalRow()]);
    this.setColumnDefs();
  }

  getTotalRow(): PartnerBudgetTableEntry {
    this.table.computeTotal();
    return new PartnerBudgetTableEntry({
      description: this.translateService.instant('project.partner.budget.table.total'),
    });
  }

  private setColumnDefs(): void {
    const columnDefs: ColDef[] = [
      {
        headerName: this.translateService.instant(
          this.table.type === PartnerBudgetTableType.STAFF
            ? 'project.partner.budget.table.staff.description'
            : 'project.partner.budget.table.description'
        ),
        field: 'description',
        editable: params => BudgetTableComponent.editableRow(this.editable, params.node),
        sortable: true,
        singleClickEdit: true,
        valueGetter: (params: any) => this.languageService.getInputValue(params.data.description),
        valueSetter: (params: any) => {
          this.languageService.updateInputValue(
            params.newValue,
            params.data.description,
            PartnerBudgetTableEntry.validDescription(params.newValue)
          );
          params.data.validDescription = params.data.description.isValid();
          return true;
        },
        cellRendererFramework: AgGridTemplateRendererComponent,
        cellRendererParams: {
          ngTemplate: this.descriptionCell
        },
        flex: 2,
      },
      {
        headerName: this.translateService.instant('project.partner.budget.table.number.of.units'),
        field: 'numberOfUnits',
        editable: params => BudgetTableComponent.editableRow(this.editable, params.node),
        sortable: true,
        singleClickEdit: true,
        type: 'numericColumn',
        valueGetter: (params: any) => Numbers.toLocale(params.data.numberOfUnits, this.locale),
        valueSetter: (params: any) => {
          params.data.setNumberOfUnits(params.newValue);
          return true;
        },
        cellStyle: (params: any) => params.data.validNumberOfUnits ? {color: 'black'} : {color: 'red'},
        flex: 1
      },
      {
        headerName: this.translateService.instant('project.partner.budget.table.price.per.unit'),
        field: 'pricePerUnit',
        editable: params => BudgetTableComponent.editableRow(this.editable, params.node),
        sortable: true,
        singleClickEdit: true,
        type: 'numericColumn',
        valueGetter: (params: any) => Numbers.toLocale(params.data.pricePerUnit, this.locale),
        valueSetter: (params: any) => {
          params.data.setPricePerUnit(params.newValue);
          return true;
        },
        cellStyle: (params: any) => params.data.validPricePerUnit ? {color: 'black'} : {color: 'red'},
        flex: 1,
      },
      {
        headerName: this.translateService.instant('project.partner.budget.table.total'),
        field: 'total',
        type: 'numericColumn',
        valueGetter: (params: any) => Numbers.toLocale(params.data.total, this.locale),
        sortable: true,
        cellStyle: (params: any) => params.data.validTotal ? {color: 'black'} : {color: 'red'},
        pinnedRowCellRendererFramework: AgGridTemplateRendererComponent,
        pinnedRowCellRendererParams: {
          ngTemplate: this.totalCell
        },
        flex: 2
      }
    ];
    if (this.editable) {
      columnDefs.push({
        headerName: '',
        colId: 'delete',
        cellRendererFramework: AgGridTemplateRendererComponent,
        cellRendererParams: {
          ngTemplate: this.deleteCell
        },
        suppressNavigable: true,
        width: 30
      });
    }
    this.gridApi?.setColumnDefs(columnDefs);
  }

  isDescriptionValid(description: any, language: InputTranslation.LanguageEnum): boolean {
    if (!description.inputs) {
      return true;
    }
    return (description as MultiLanguageInput).isValidForLanguage(language);
  }
}
