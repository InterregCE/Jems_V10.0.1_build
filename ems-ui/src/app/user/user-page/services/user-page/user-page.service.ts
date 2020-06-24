import {Injectable} from '@angular/core';
import {AccountService, OutputAccount, PageOutputAccount} from '@cat/api';
import {Observable, ReplaySubject} from 'rxjs';
import {flatMap, map} from 'rxjs/operators';

@Injectable()
export class UserPageService {

  private page$ = new ReplaySubject<any>(1);
  private filtered$ = this.page$
    .pipe(
      flatMap((page) => this.accountService.list(page.page, page.size, page.sort)),
      map((page: PageOutputAccount) => page.content)
    );

  constructor(private accountService: AccountService) {
  }

  filtered(): Observable<OutputAccount[]> {
    return this.filtered$;
  }

  newPage(page?: number, size?: number, sort?: string): void {
    this.page$.next({page, size, sort});
  }
}
