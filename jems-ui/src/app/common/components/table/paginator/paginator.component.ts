import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {Tables} from '../../../utils/tables';
import {PageEvent} from '@angular/material/paginator';

@Component({
  selector: 'jems-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaginatorComponent {
  Tables = Tables;

  @Input()
  length: number;
  @Input()
  currentPageIndex: number = Tables.DEFAULT_INITIAL_PAGE_INDEX;
  @Input()
  currentPageSize: number = Tables.DEFAULT_INITIAL_PAGE_SIZE;
  @Output()
  pageIndexChanged: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  pageSizeChanged: EventEmitter<number> = new EventEmitter<number>();

  newPage(pageEvent: PageEvent): void {
    if (pageEvent.pageSize !== this.currentPageSize) {
      this.currentPageSize = pageEvent.pageSize;
      this.pageSizeChanged.emit(this.currentPageSize);
    }
    if (pageEvent.pageIndex !== this.currentPageIndex) {
      this.currentPageIndex = pageEvent.pageIndex;
      this.pageIndexChanged.emit(this.currentPageIndex);
    }
  }
}
