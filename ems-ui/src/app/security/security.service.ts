import {Injectable} from '@angular/core';
import {AuthenticationService, LoginRequest, OutputCurrentUser, UserService, OutputUser} from '@cat/api';
import {from, Observable, of, ReplaySubject} from 'rxjs';
import {catchError, flatMap, map, shareReplay, take, tap} from 'rxjs/operators';
import {Log} from '../common/utils/log';

@Injectable({providedIn: 'root'})
export class SecurityService {

  private myCurrentUser: ReplaySubject<OutputCurrentUser | null> = new ReplaySubject(1);
  public hasBeenInitialized = false;

  private currentUserDetails$ = this.myCurrentUser
    .pipe(
      map(user => user?.id),
      tap(() => this.hasBeenInitialized = true),
      flatMap(id => (id && id > 0) ? this.userService.getById(id) : of(null)),
      tap(user => Log.info('Current user details loaded', this, user)),
      shareReplay(1)
    );

  constructor(private authenticationService: AuthenticationService,
              private userService: UserService) {
  }

  get currentUser(): Observable<OutputCurrentUser | null> {
    return this.myCurrentUser
      .pipe(
        map((user) => user && user.name ? user : null)
      );
  }

  get currentUserDetails(): Observable<OutputUser | null> {
    return this.currentUserDetails$;
  }

  isLoggedIn(): Observable<boolean> {
    return from(this.myCurrentUser)
      .pipe(map((user) => !!(user && user.name)));
  }

  login(loginRequest: LoginRequest): Observable<OutputCurrentUser | null> {
    return this.authenticationService.login(loginRequest)
      .pipe(
        take(1),
        tap(user => Log.info('User logged in', this, user)),
        tap((user: OutputCurrentUser) => this.myCurrentUser.next(user)),
      );
  }

  reloadCurrentUser(): Observable<OutputCurrentUser> {
    return this.authenticationService.getCurrentUser()
      .pipe(
        take(1),
        tap(user => this.myCurrentUser.next(user)),
        tap(user => Log.info('Current user loaded', this, user)),
        catchError(err => {
          this.myCurrentUser.next(null);
          throw err;
        })
      );
  }

  clearAuthentication(): void {
    this.myCurrentUser.next(null);
  }

  logout() {
    return this.authenticationService.logout()
      .pipe(
        take(1),
        tap(() => Log.info('Current user logged out', this)),
        tap(() => this.clearAuthentication()),
      )
  }
}
