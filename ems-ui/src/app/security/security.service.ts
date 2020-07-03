import {Injectable} from '@angular/core';
import {AuthenticationService, LoginRequest, OutputCurrentUser} from '@cat/api';
import {AuthenticationHolder} from './authentication-holder.service';
import {from, Observable, ReplaySubject} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {Log} from '../common/utils/log';

@Injectable({providedIn: 'root'})
export class SecurityService {

  private myCurrentUser: ReplaySubject<OutputCurrentUser | null> = new ReplaySubject(1);

  constructor(private authenticationHolder: AuthenticationHolder,
              private authenticationService: AuthenticationService) {
  }

  get currentUser(): Observable<OutputCurrentUser | null> {
    return this.myCurrentUser
      .pipe(
        map((user) => user && user.name ? user : null)
      );
  }


  isLoggedIn(): Observable<boolean> {
    return from(this.myCurrentUser)
      .pipe(map((user) => !!(user && user.name)));
  }

  login(loginRequest: LoginRequest): Observable<OutputCurrentUser | null> {
    return this.authenticationService.login(loginRequest)
      .pipe(
        tap(user => Log.info('User logged in', this, user)),
        tap((user: OutputCurrentUser) => this.authenticationHolder.currentUserId = user.id),
        tap((user: OutputCurrentUser) => this.authenticationHolder.currentUsername = user.name),
        tap((user: OutputCurrentUser) => this.myCurrentUser.next(user)),
      );
  }

  reloadCurrentUser(): void {
    this.authenticationService.getCurrentUser()
      .pipe(
        tap(user => Log.info('Current user loaded', this, user))
      )
      .subscribe(
        (value: OutputCurrentUser) => this.myCurrentUser.next(value),
        () => this.myCurrentUser.next(null)
      );
  }

  clearAuthentication(): void {
    this.authenticationHolder.currentUsername = null;
    this.myCurrentUser.next(null);
  }

  async logout() {
    this.clearAuthentication();
    await this.authenticationService.logout()
      .pipe(
        tap(() => Log.info('Current user logged out', this, this.authenticationHolder))
      )
      .toPromise();
  }
}
