import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Router} from '@angular/router';
import {SecurityService} from './security.service';
import {LoginPageService} from '../authentication/login/services/login-page-service';

@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {
  constructor(private securityService: SecurityService,
              private loginPageService: LoginPageService,
              private router: Router) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(
      req.clone({
        headers: req.headers.append('X-Requested-With', 'XMLHttpRequest')
      }))
      .pipe(
        catchError(err => {
          if (err.status === 401) {
            // go to login when unauthorized status codes are intercepted
            if (this.router.url !== '/login') {
              this.loginPageService.newAuthenticationError({i18nKey: 'authentication.expired', httpStatus: 401});
              this.router.navigate(['/login']);
            }
            this.securityService.clearAuthentication();
          }
          // TODO handle err.status 403
          return throwError(err);
        }));
  }
}
