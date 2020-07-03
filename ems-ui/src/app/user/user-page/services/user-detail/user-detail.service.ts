import {Injectable} from '@angular/core';
import {OutputUser} from '@cat/api';
import {Observable, Subject} from 'rxjs';


@Injectable()
export class UserDetailService {
  private userSaved$ = new Subject<OutputUser>();

  userSavedEvent(): Observable<OutputUser> {
    return this.userSaved$.asObservable();
  }

  userSaved(user: OutputUser): void {
    this.userSaved$.next(user);
  }
}
