import {AfterContentChecked, AfterViewInit, Directive, ElementRef, Input} from '@angular/core';

@Directive({
  selector: '[appTableConfig]'
})
export class TableConfigDirective implements AfterViewInit, AfterContentChecked {

  @Input() widthConfig: string[];
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
    for (const row of rows) {
      const columns = row.children;
      for (let i = 0; i < columns.length; ++i) {
        if (this.widthConfig[i] && this.widthConfig[i].length > 0) {
          columns[i].classList.add(this.widthConfig[i]);
        }
      }
    }

  }

}
