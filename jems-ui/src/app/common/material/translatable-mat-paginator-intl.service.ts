import {MatPaginatorIntl} from '@angular/material/paginator';
import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class TranslatableMatPaginatorIntl extends MatPaginatorIntl {

  rangeLabel: string;

  constructor(private translateService: TranslateService) {
    super();
    this.translateService.stream([
        'common.paginator.range.label',
        'common.paginator.items.per.page.label',
        'common.paginator.next.page.label',
        'common.paginator.previous.page.label',
        'common.paginator.first.page.label',
        'common.paginator.last.page.label'
      ]
    ).subscribe((value) => {
        this.rangeLabel = value['common.paginator.range.label'];
        this.itemsPerPageLabel = value['common.paginator.items.per.page.label'];
        this.nextPageLabel = value['common.paginator.next.page.label'];
        this.previousPageLabel = value['common.paginator.previous.page.label'];
        this.firstPageLabel = value['common.paginator.first.page.label'];
        this.lastPageLabel = value['common.paginator.last.page.label'];
        this.changes.next();
      }
    );
  }

  getRangeLabel = (page: number, pageSize: number, length: number) => {
    if (length === 0 || pageSize === 0) {
      return `0 ${this.rangeLabel} ` + length;
    }
    const lengthMax = Math.max(length, 0);
    const startIndex = page * pageSize;
    const endIndex = startIndex < lengthMax ?
      Math.min(startIndex + pageSize, lengthMax) :
      startIndex + pageSize;
    return `${startIndex + 1} - ${endIndex} ${this.rangeLabel} ${lengthMax}`;
  }
}
