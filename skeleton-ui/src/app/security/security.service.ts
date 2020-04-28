import {Injectable} from '@angular/core';
import {OutputUser, UserService} from '@cat/api';
import {AuthenticationService} from './authentication.service';
import {from, Observable, ReplaySubject, Subject} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class SecurityService {

  private myCurrentUser: ReplaySubject<OutputUser | null> = new ReplaySubject(1);

  constructor(private authenticationHolder: AuthenticationService,
              private userApi: UserService) {
    this.reloadCurrentUser();
  }

  get currentUser(): Observable<OutputUser | null> {
    return this.myCurrentUser.asObservable();
  }

  isLoggedIn(): Observable<boolean> {
    return from(this.myCurrentUser)
      .pipe(map((user) => !!user));
  }

  async login(username: string) {
    this.authenticationHolder.currentUsername = username;
    await this.reloadCurrentUser().toPromise();
  }

  private reloadCurrentUser(): Observable<any> {
    const loadingCurrentUser = this.userApi.getCurrentUser();
    const doneProcessingSubject = new Subject();
    loadingCurrentUser.subscribe(
      value => { this.myCurrentUser.next(value); doneProcessingSubject.complete(); },
      () => { this.myCurrentUser.next(null); doneProcessingSubject.complete(); });
    return doneProcessingSubject.asObservable();
  }

  async logout() {
    this.authenticationHolder.currentUsername = null;
    this.myCurrentUser.next(null);
    await fetch('/logout', { method: 'POST' });
  }

}
