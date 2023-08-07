import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {TableConfiguration} from './model/table.configuration';
import {ColumnConfiguration} from './model/column.configuration';
import {ColumnType} from './model/column-type.enum';
import {Observable} from 'rxjs';
import {Tools} from '../../utils/tools';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../utils/tables';
import {MoneyPipe} from '../../pipe/money.pipe';
import {LanguageStore} from '../../services/language-store.service';
import {InputTranslation, ProjectVersionDTO} from '@cat/api';
import {ColumnWidth} from './model/column-width';
import {LocaleDatePipe} from '../../pipe/locale-date.pipe';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {LongDateFormatKey} from 'moment';

@UntilDestroy()

@Component({
  selector: 'jems-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit, OnChanges {
  ColumnType = ColumnType;
  ColumnWidth = ColumnWidth;

  @Input()
  configuration: TableConfiguration;
  @Input()
  rows: Observable<any[]> | any[];
  @Input()
  totalElements: number;
  @Input()
  pageIndex: number;
  @Input()
  confirmPageChange = false;
  @Input()
  isMultiLanguage = false;

  @Output()
  sortRows = new EventEmitter<Partial<MatSort>>();
  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();

  @ViewChild(MatSort) matSort: MatSort;

  columnsToDisplay: string[] = [];
  currentPageSize = Tables.DEFAULT_INITIAL_PAGE_SIZE;
  selectedVersion: ProjectVersionDTO | undefined;

  constructor(private moneyPipe: MoneyPipe,
              private localeDatePipe: LocaleDatePipe,
              public languageStore: LanguageStore,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private versionStore: ProjectVersionStore) {
  }

  ngOnInit(): void {
    this.columnsToDisplay = this.configuration.columns.map(col => col.displayedColumn);
    this.versionStore.selectedVersion$.pipe(untilDestroyed(this))
      .subscribe((selectedVersion) => this.selectedVersion = selectedVersion);
  }

  /**
   * formats element value with certain date format, translation key or its pure value.
   *
   * @param column configuration
   * @param element value
   * @param currentSystemLanguage current system language
   */
  formatColumnValue(column: ColumnConfiguration, element: any, currentSystemLanguage: string): any {
    if (!column.elementProperty) {
      return element;
    }
    if (column.i18nFixedKey) {
      return column.i18nFixedKey;
    }
    if (column.columnType === ColumnType.InputTranslation) {
      const elementInSystemLang = element[column.elementProperty]
        .find((it: InputTranslation) => it.language === currentSystemLanguage);
      return !!elementInSystemLang ? elementInSystemLang.translation : '';
    }
    const elementValue = Tools.getChainedProperty(element, column.elementProperty, '');
    if (column.elementTranslationKey) {
      return `${column.elementTranslationKey}.${elementValue}`;
    }
    if (column.alternativeValueCondition && column.alternativeValueCondition(elementValue)) {
      return column.alternativeValue;
    }
    if (column.columnType === ColumnType.DateOnlyColumn) {
      return this.localeDatePipe.transform(elementValue, 'L');
    }
    if (column.columnType === ColumnType.DateColumn) {
      return this.localeDatePipe.transform(elementValue, 'L', 'LT');
    }
    if (column.columnType === ColumnType.DateColumnWithSeconds) {
      return this.localeDatePipe.transform(elementValue, 'L', 'LTS');
    }
    if (column.columnType === ColumnType.Decimal) {
      return this.moneyPipe.transform(elementValue);
    }
    if (column.columnType === ColumnType.DecimalWithJustifiedStart) {
      return this.moneyPipe.transform(elementValue);
    }
    return elementValue;
  }

  formatOnlyTimeOrDate(column: ColumnConfiguration, element: any, dateFormat: LongDateFormatKey) {
    if (!column.elementProperty) {
      return element;
    }
    const elementValue = Tools.getChainedProperty(element, column.elementProperty, '');
    return this.localeDatePipe.transform(elementValue, dateFormat);
  }

  getI18nArgs(column: ColumnConfiguration, element: any): any {
    if (column.i18nArgs) {
      return column.i18nArgs(element);
    }
    return null;
  }

  /**
   * formats tooltip value with certain translation key or its pure value.
   *
   * @param column configuration
   * @param element value
   */
  formatColumnTooltip(column: ColumnConfiguration, element: any): any {
    if (!column.tooltip?.tooltipContent) {
      return element;
    }
    const elementTitle = Tools.getChainedProperty(element, column.tooltip.tooltipContent, '');
    if (column.tooltip.tooltipTranslationKey) {
      return `${column.tooltip.tooltipTranslationKey}.${elementTitle}`;
    }
    return elementTitle;
  }

  rowClicked(row: any): void {
    if(!this.configuration.isTableClickable) {
      return;
    }

    let queryParams = {};
    if(this.selectedVersion !== undefined && !this.selectedVersion?.current) {
      queryParams = {queryParams: {version: this.selectedVersion?.version}};
    }

    if (this.configuration.extraPathParamFields && this.configuration.extraPathParamFields.length > 0) {
      this.configuration.extraPathParamFields.forEach((element) => {
        this.configuration.routerLink = this.configuration.routerLink?.replace(`{${element}}`, row[element]);
      });
      this.routingService.navigate([this.configuration.routerLink], {...queryParams, relativeTo: this.activatedRoute});
    } else {
      this.routingService.navigate([this.configuration.routerLink, row.id], {...queryParams, relativeTo: this.activatedRoute});
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.configuration) {
      this.columnsToDisplay = this.configuration.columns.map(col => col.displayedColumn);
    }
  }
}
