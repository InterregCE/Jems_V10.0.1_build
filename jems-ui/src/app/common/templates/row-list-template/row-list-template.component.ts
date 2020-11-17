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
  @Input() appearance: string[];

  ngAfterViewInit(): void {
    if (!this.container.nativeElement) { return; }

    const rows = this.container.nativeElement.children;
    for (let j = 0; j < rows.length; ++j) {
      const columns = rows[j].children;
      if (this.appearance && this.appearance[j] && this.appearance[j].length > 0) {
        rows[j].classList.add(this.appearance[j]);
      }
      for (let i = 0; i < columns.length; ++i) {
        if (this.columnClassList[i] && this.columnClassList[i].length > 0) {
          columns[i].classList.add(this.columnClassList[i]);
        }
      }
    }
  }
}
