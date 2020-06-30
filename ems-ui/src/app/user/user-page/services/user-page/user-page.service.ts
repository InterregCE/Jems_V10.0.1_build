import {Injectable} from '@angular/core';
import {OutputUser, UserService} from '@cat/api';
import {merge, Observable, ReplaySubject} from 'rxjs';
import {flatMap, map, shareReplay, tap} from 'rxjs/operators';
import {UserDetailService} from '../user-detail/user-detail.service';

@Injectable()
export class UserPageService {

  private initialPage = {page: 0, size: 100, sort: 'id,desc'};
  private currentPage$ = new ReplaySubject<any>(1);
  private userSaveAsPage$ = this.userDetailService.saveSuccess()
    .pipe(
      map(() => this.initialPage)
    )

  private userList$ =
    merge(
      this.userSaveAsPage$,
      this.currentPage$
    )
      .pipe(
        flatMap(page => this.userService.list(page?.page, page?.size, page?.sort)),
        tap(page => console.log('Fetched the users:', page.content)),
        map(page => page.content),
        shareReplay(1)
      );

  constructor(private userService: UserService,
              private userDetailService: UserDetailService) {
    this.newPage(this.initialPage.page, this.initialPage.size, this.initialPage.sort)
  }

  /**
   * Returns a list of users observable.
   * The last fetched list is emitted to all/late subscribers - shareReplay(1).
   * The list is refreshed when:
   * - newPage is called
   * - a user is successfully saved
   * - ...?
   */
  userList(): Observable<OutputUser[]> {
    return this.userList$;
  }

  newPage(page?: number, size?: number, sort?: string): void {
    this.currentPage$.next({page, size, sort});
  }
}
