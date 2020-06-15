import {Injectable} from '@angular/core';
import {AuthenticationService, LoginRequest, OutputCurrentUser} from '@cat/api';
import {AuthenticationHolder} from './authentication-holder.service';
import {from, Observable, ReplaySubject, Subject} from 'rxjs';
import {map, tap} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class SecurityService {

  private myCurrentUser: ReplaySubject<OutputCurrentUser | null> = new ReplaySubject(1);

  constructor(private authenticationHolder: AuthenticationHolder,
              private authenticationService: AuthenticationService) {
    this.reloadCurrentUser();
  }

  get currentUser(): Observable<OutputCurrentUser | null> {
    return this.myCurrentUser.asObservable();
  }

  isLoggedIn(): Observable<boolean> {
    return from(this.myCurrentUser)
      .pipe(map((user) => !!user));
  }

  login(loginRequest: LoginRequest): Observable<OutputCurrentUser | null> {
    return this.authenticationService.login(loginRequest)
      .pipe(
        tap((user: OutputCurrentUser) => {
          this.authenticationHolder.currentUsername = user.name;
          this.myCurrentUser.next(user);
        })
      );
  }

  private reloadCurrentUser(): Observable<any> {
    const loadingCurrentUser = this.authenticationService.getCurrentUser();
    const doneProcessingSubject = new Subject();
    loadingCurrentUser.subscribe(
      (value: OutputCurrentUser) => {
        this.myCurrentUser.next(value);
        doneProcessingSubject.complete();
      },
      () => {
        this.myCurrentUser.next(null);
        doneProcessingSubject.complete();
      });
    return doneProcessingSubject.asObservable();
  }

  async logout() {
    this.authenticationHolder.currentUsername = null;
    this.myCurrentUser.next(null);
    await this.authenticationService.logout()
      .toPromise()
      .then(() => console.log('logged out'));
  }
}
