import {
  AfterViewInit,
  Component,
  ComponentFactory,
  ComponentFactoryResolver,
  ComponentRef,
  Input,
  OnInit,
  QueryList,
  ViewChildren,
  ViewContainerRef
} from '@angular/core';
import {TableConfiguration} from '../configurations/table.configuration';
import {DatePipe} from '@angular/common';
import {ColumnConfiguration} from '../configurations/column.configuration';
import {DescriptionCellComponent} from '../cell-renderers/description-cell/description-cell.component';
import {ColumnType} from '../enums/column-type.enum';
import {Observable} from 'rxjs';
import {Tools} from '../utils/tools';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
  providers: [TranslatePipe]
})
export class TableComponent implements OnInit, AfterViewInit {
  @Input()
  configuration: TableConfiguration;
  @Input()
  rows: Observable<any[]> | any[];

  @ViewChildren('customColumn', {read: ViewContainerRef}) customComponent: QueryList<ViewContainerRef>;

  factory: ComponentFactory<any>;
  componentRefList: ComponentRef<any>[] = [];
  columnType = ColumnType;
  customComponentColumn: ColumnConfiguration;
  columnsToDisplay: string[] = [];

  constructor(private datepipe: DatePipe,
              private translatePipe: TranslatePipe,
              private resolver: ComponentFactoryResolver) {
  }

  ngOnInit(): void {
    this.configuration.columns.forEach(column => {
      if (column.columnType === this.columnType.CustomComponent && column.component === DescriptionCellComponent) {
        this.factory = this.resolver.resolveComponentFactory(column.component);
        this.customComponentColumn = column;
      }
    });

    this.configuration.columns.forEach((column: ColumnConfiguration) => {
      this.columnsToDisplay.push(column.displayedColumn);
    });
    if (this.configuration.actionColumn) {
      this.columnsToDisplay.push('Actions');
    }
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.createCustomComponents(), 0);
  }

  formatColumnValue(column: ColumnConfiguration, element: any): any {
    const elementValue = Tools.getChainedProperty(element, column.elementProperty, '');
    if (column.i18nHeader) {
      return this.translatePipe.transform(column.i18nHeader + elementValue);
    }
    if (column.columnType === ColumnType.Date) {
      return this.datepipe.transform(elementValue, 'yyyy-MM-dd HH:mm:ss');
    }
    return elementValue;
  }

  changeCustomColumnData(index: number, extraProps: any): void {
    this.componentRefList[index].instance.data.extraProps = extraProps;
  }

  createCustomComponents(): void {
    this.componentRefList = [];
    for (let i = 0; i < this.customComponent.length; i++) {
      const componentRef = this.customComponent.toArray()[i].createComponent(this.factory);
      componentRef.instance.data = {
        index: i,
        row: this.configuration.dataSource.data[i],
        extraProps: this.customComponentColumn.extraProps,
      };
      this.componentRefList.push(componentRef);
    }
  }
}
