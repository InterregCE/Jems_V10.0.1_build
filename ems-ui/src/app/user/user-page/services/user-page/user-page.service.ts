import {Injectable} from '@angular/core';
import {OutputUser, PageOutputUser, UserService} from '@cat/api';
import {Observable, ReplaySubject} from 'rxjs';
import {flatMap, map} from 'rxjs/operators';

@Injectable()
export class UserPageService {

  private page$ = new ReplaySubject<any>(1);
  private filtered$ = this.page$
    .pipe(
      flatMap((page) => this.userService.list(page.page, page.size, page.sort)),
      map((page: PageOutputUser) => page.content)
    );

  constructor(private userService: UserService) {
  }

  filtered(): Observable<OutputUser[]> {
    return this.filtered$;
  }

  newPage(page?: number, size?: number, sort?: string): void {
    this.page$.next({page, size, sort});
  }
}
