import {AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, Input, ViewChild} from '@angular/core';

@Component({
  selector: 'app-row-list-template',
  templateUrl: './row-list-template.component.html',
  styleUrls: ['./row-list-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RowListTemplateComponent implements AfterViewInit {

  @ViewChild('container') container: ElementRef;
  @Input() columnClassList: string[];

  ngAfterViewInit(): void {
    if (!this.container.nativeElement) { return; }

    const rows = this.container.nativeElement.children;
    for (const row of rows) {
      const columns = row.children;
      for (let i = 0; i < columns.length; ++i) {
        if (this.columnClassList[i] && this.columnClassList[i].length > 0) {
          columns[i].classList.add(this.columnClassList[i]);
        }
      }
    }
  }
}
