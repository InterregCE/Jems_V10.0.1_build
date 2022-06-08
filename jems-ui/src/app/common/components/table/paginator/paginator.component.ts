import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {Tables} from '../../../utils/tables';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {Forms} from "@common/utils/forms";
import {filter, take, tap} from "rxjs/operators";
import {Log} from "@common/utils/log";
import {MatDialog} from "@angular/material/dialog";

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
  @Input()
  confirmPageChange: boolean = false;

  @Output()
  pageIndexChanged: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  pageSizeChanged: EventEmitter<number> = new EventEmitter<number>();

  @ViewChild('paginator', {static: true})
  paginator: MatPaginator;

  constructor(private dialog: MatDialog) {
  }

  newPage(pageEvent: PageEvent): void {
    if (!this.confirmPageChange) {
      this.changePage(pageEvent);
      return;
    }

    Forms.confirm(
      this.dialog,
      {
        title: 'common.sidebar.dialog.title',
        warnMessage: 'common.sidebar.dialog.message'
      }
    ).pipe(
      take(1),
      tap(confirmed => {
        if (confirmed) {
          this.changePage(pageEvent);
          return;
        }
        setTimeout(() => {
          this.paginator.pageSize = this.currentPageSize;
          this.paginator.pageIndex = this.currentPageIndex;
        });
      })
    ).subscribe();
  }

  private changePage(pageEvent: PageEvent): void {
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
