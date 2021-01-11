import {AfterContentChecked, AfterViewInit, Directive, ElementRef, Input} from '@angular/core';
import {WidthConfig} from './WidthConfig';

const DEFAULT_MIN_WIDTH = 10;

@Directive({
  selector: '[appTableConfig]'
})
export class TableConfigDirective implements AfterViewInit, AfterContentChecked {

  @Input() widthConfig: WidthConfig[];
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
        if (this.widthConfig[i]) {
          if (this.widthConfig[i].minInRem) {
            columns[i].classList.add(`min-width-${this.widthConfig[i].minInRem}`);
          }
          if (this.widthConfig[i].maxInRem) {
            columns[i].classList.add(`max-width-${this.widthConfig[i].maxInRem}`);
          }
        }
      }
    }
  }

  private calculateRowMinWidthToSupportStickyColumns(): number {
    const cellsRightMargin = 1;
    const firstAndLastCellsPadding = 2;
    return (this.widthConfig.map(config => config.minInRem).reduce((accumulator, minValue) => (accumulator ? accumulator : 0) + (minValue ? minValue : 0) + cellsRightMargin, 0) || 0) + firstAndLastCellsPadding;
  }
}
