import {AfterContentChecked, AfterViewInit, Directive, ElementRef, Input} from '@angular/core';
import {TableConfig} from './TableConfig';

@Directive({
  selector: '[appTableConfig]'
})
export class TableConfigDirective implements AfterViewInit, AfterContentChecked {

  @Input('appTableConfig') tableConfig: TableConfig[];
  @Input() appearance: 'table' | 'material-table' | null = null;


  constructor(private el: ElementRef) {
  }

  ngAfterViewInit(): void {
    this.setClasses();
  }

  ngAfterContentChecked(): void {
    this.setClasses();
  }

  setClasses(): void {
    this.el.nativeElement.classList.add(`app-table-config`);
    if (this.appearance) {
      this.el.nativeElement.classList.add(`${this.appearance}-appearance`);
    }
    const rows = this.el.nativeElement.children;
    const rowMinWidthToSupportStickyColumns = this.calculateRowMinWidthToSupportStickyColumns();
    for (const row of rows) {
      row.style.minWidth = `${rowMinWidthToSupportStickyColumns}rem`;
      const columns = row.children;
      for (let i = 0; i < columns.length; ++i) {
        if (this.tableConfig[i]) {
          if (this.tableConfig[i].minInRem) {
            columns[i].classList.add(`min-width-${this.tableConfig[i].minInRem}`);
          }
          if (this.tableConfig[i].maxInRem) {
            columns[i].classList.add(`max-width-${this.tableConfig[i].maxInRem}`);
          }
        }
      }
    }
  }

  private calculateRowMinWidthToSupportStickyColumns(): number {
    const cellsRightAndLeftPadding = 1;
    const firstAndLastCellsPadding = 1.5;
    return (this.tableConfig.map(config => config.minInRem).reduce((accumulator, minValue) => (accumulator || 0) + (minValue || 0) + cellsRightAndLeftPadding, 0) || 0) + firstAndLastCellsPadding;
  }
}
