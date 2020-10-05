import {Injectable} from '@angular/core';
import {LoginRequest} from '@cat/api';
import {catchError, take, takeUntil, tap} from 'rxjs/operators';
import {SecurityService} from '../../../security/security.service';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {BaseComponent} from '@common/components/base-component';
import {AuthenticationStore} from '../../service/authentication-store.service';

@Injectable()
export class LoginPageService extends BaseComponent {


  constructor(private securityService: SecurityService,
              private authenticationStore: AuthenticationStore,
              private route: ActivatedRoute,
              private router: Router) {
    super();
  }

  login(loginRequest: LoginRequest): void {

    const queryRef = this.route.snapshot.queryParamMap.get('ref')
    const redirectTo = queryRef ? [queryRef] : ['app']

    this.securityService.login(loginRequest)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.authenticationStore.newAuthenticationError(null)),
        tap(() => this.router.navigate(redirectTo)),
        catchError((error: HttpErrorResponse) => {
          this.authenticationStore.newAuthenticationError(error.error);
          throw error;
        })
      )
      .subscribe();
  }
}
