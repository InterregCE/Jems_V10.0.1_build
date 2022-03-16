import {AfterContentChecked, AfterViewInit, Directive, ElementRef, Input} from '@angular/core';
import {TableConfig} from './TableConfig';

interface StickyConfig {
  stickyLeftPosition: number;
  paddingToAdd: number;
  lastStickyColumn: any;
}

@Directive({
  selector: '[jemsTableConfig]'
})
export class TableConfigDirective implements AfterViewInit, AfterContentChecked {

  @Input('jemsTableConfig') tableConfig: TableConfig[] = [];

  private firstColLeftPaddingInRem = 1;
  @Input('lastColRightPaddingInRem')
  private lastColRightPaddingInRem = 1;
  private colsRightPaddingInRem = 0.5;
  private colsLeftPaddingInRem = 0.5;


  constructor(private el: ElementRef) {
  }

  ngAfterViewInit(): void {
    this.setClasses();
  }

  ngAfterContentChecked(): void {
    this.setClasses();
  }

  setClasses(): void {
    this.el.nativeElement.classList.add(`jems-table-config`);
    this.el.nativeElement.classList.add(`material-table-appearance`);

    this.el.nativeElement.style.setProperty('--first-col-left-padding', `${this.firstColLeftPaddingInRem}rem`);
    this.el.nativeElement.style.setProperty('--last-col-right-padding', `${this.lastColRightPaddingInRem}rem`);
    this.el.nativeElement.style.setProperty('--cols-right-padding', `${this.colsRightPaddingInRem}rem`);
    this.el.nativeElement.style.setProperty('--cols-left-padding', `${this.colsLeftPaddingInRem}rem`);

    this.tableConfig = this.tableConfig || [];

    const rows = this.el.nativeElement.children;
    const rowMinWidthToSupportStickyColumns = this.calculateRowMinWidthToSupportStickyColumns();
    for (const row of rows) {
      row.style.minWidth = `${rowMinWidthToSupportStickyColumns}rem`;
      const columns = row.children;

      const stickyEndColumns = [];
      let stickyConfig = {
        stickyLeftPosition: 0,
        paddingToAdd: this.firstColLeftPaddingInRem + this.colsRightPaddingInRem,
        lastStickyColumn: null,
      } as StickyConfig;

      for (let i = 0; i < columns.length; ++i) {
        if (this.tableConfig[i]) {
          if (this.tableConfig[i].minInRem) {
            columns[i].classList.add(`min-width-${this.tableConfig[i].minInRem}`);
          }
          if (this.tableConfig[i].maxInRem) {
            columns[i].classList.add(`max-width-${this.tableConfig[i].maxInRem}`);
          }

          /// for handling sticky and stickyEnd columns
          // note that for stickies to work all the columns should have a min-with defined
          if (columns[i].className.indexOf('mat-table-sticky') >= 0) {
            if (columns[i].style.right) { // it's a stickyEnd column
              stickyEndColumns.unshift({
                column: columns[i],
                minRem: (this.tableConfig[i].minInRem || 0)
              });
            } else { // it's a sticky column
              stickyConfig = this.handleStickyColumn(columns[i], (this.tableConfig[i].minInRem || 0), stickyConfig);
            }
          }
        }
      }

      // so we can add right-border to the last sticky column
      if (stickyConfig.lastStickyColumn !== null) {
        stickyConfig.lastStickyColumn.classList.add('last-left-sticky-column');
      }

      // it should be call here since we need to go in a reverse order for setting the right position for the stickyEnd columns
      this.handleStickyEndColumns(stickyEndColumns);
    }
  }

  private calculateRowMinWidthToSupportStickyColumns(): number {
    const cellsRightAndLeftPadding = this.colsRightPaddingInRem + this.colsLeftPaddingInRem;
    const firstAndLastCellsPadding = (this.firstColLeftPaddingInRem - this.colsLeftPaddingInRem) + (this.lastColRightPaddingInRem - this.colsRightPaddingInRem);
    return (this.tableConfig.map(config => config.minInRem).reduce((accumulator, minValue) => (accumulator || 0) + (minValue || 0) + cellsRightAndLeftPadding, 0) || 0) + firstAndLastCellsPadding;
  }

  private handleStickyColumn(column: any, columnMinInRem: number, stickyConfig: StickyConfig): StickyConfig {
    column.style.left = `${stickyConfig.stickyLeftPosition}rem`;
    column.style.zIndex = 3;
    const nextStickyLeftPosition = stickyConfig.stickyLeftPosition + columnMinInRem + stickyConfig.paddingToAdd;
    const nextPaddingToAdd = this.colsRightPaddingInRem + this.colsLeftPaddingInRem;
    return {
      lastStickyColumn: column,
      stickyLeftPosition: nextStickyLeftPosition,
      paddingToAdd: nextPaddingToAdd
    } as StickyConfig;
  }

  private handleStickyEndColumns(stickyEndColumns: any[]): void {
    let stickyRightPosition = 0;
    let paddingToAdd = this.lastColRightPaddingInRem + this.colsLeftPaddingInRem;
    let firstStickyEndColumn = null;
    for (const stickyCol of stickyEndColumns) {
      stickyCol.column.style.right = `${stickyRightPosition}rem`;
      stickyRightPosition = stickyRightPosition + stickyCol.minRem + paddingToAdd;
      paddingToAdd = this.colsLeftPaddingInRem + this.colsRightPaddingInRem;
      firstStickyEndColumn = stickyCol.column;
    }
    if (firstStickyEndColumn !== null) { // so we can add left-border to the first stickyEnd column
      firstStickyEndColumn.classList.add('first-sticky-end-column');
    }
  }
}
