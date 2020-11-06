import {MatPaginatorIntl} from '@angular/material/paginator';
import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {LanguageService} from '../services/language.service';

@Injectable({
  providedIn: 'root'
})
export class TranslatableMatPaginatorIntl extends MatPaginatorIntl {

  constructor(private translateService: TranslateService, private languageService: LanguageService) {
    super();
    this.languageService.systemLanguage$.subscribe(() => {
      this.translateLabels();
    })
  }

  getRangeLabel = (page: number, pageSize: number, length: number) => {
    const rangeLabel = this.translateService.instant('common.paginator.range.label');
    if (length === 0 || pageSize === 0) {
      return `0 ${rangeLabel} ` + length;
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    const endIndex = startIndex < length ?
      Math.min(startIndex + pageSize, length) :
      startIndex + pageSize;
    return startIndex + 1 + ' - ' + endIndex + ` ${rangeLabel} ` + length;
  };

  translateLabels(): void {
    this.itemsPerPageLabel = this.translateService.instant('common.paginator.items.per.page.label');
    this.nextPageLabel = this.translateService.instant('common.paginator.next.page.label');
    this.previousPageLabel = this.translateService.instant('common.paginator.previous.page.label');
    this.firstPageLabel = this.translateService.instant('common.paginator.first.page.label');
    this.lastPageLabel = this.translateService.instant('common.paginator.last.page.label');
    this.changes.next();
  }
}
