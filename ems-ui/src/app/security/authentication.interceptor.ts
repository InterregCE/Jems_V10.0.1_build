import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {AuthenticationHolder} from './authentication-holder.service';
import {catchError} from 'rxjs/operators';
import {Router} from '@angular/router';
import {SecurityService} from './security.service';

@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {
  constructor(private authenticationHolder: AuthenticationHolder,
              private securityService: SecurityService,
              private router: Router) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authReq: HttpRequest<any>;
    if (!!this.authenticationHolder.currentUsername) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Basic ${btoa(this.authenticationHolder.currentUsername + ':')}`
        }
      });
    } else {
      authReq = req;
    }

    return next.handle(
      authReq.clone({
        headers: authReq.headers.append('X-Requested-With', 'XMLHttpRequest')
      }))
      .pipe(
        catchError(err => {
          if (err.status === 401 || err.status === 403) {
            // go to login when unauthorized status codes are intercepted
            if (this.router.url !== '/login') {
              this.securityService.newAuthenticationError({i18nKey: 'authentication.expired', httpStatus: 401});
              this.router.navigate(['/login']);
            }
            this.securityService.clearAuthentication();
          }
          return throwError(err);
        }));
  }
}
