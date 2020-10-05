import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Router} from '@angular/router';
import {AuthenticationStore} from '../authentication/service/authentication-store.service';

@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {
  constructor(private router: Router,
              private authenticationStore: AuthenticationStore) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req)
      .pipe(
        catchError(err => {
          if (err.status === 401) {
            // go to login when unauthorized status codes are intercepted
            if (this.router.url !== '/login') {
              this.authenticationStore.newAuthenticationError({i18nKey: 'authentication.expired', httpStatus: 401});
              this.router.navigate(['/login']);
            }
          }
          // TODO handle err.status 403
          return throwError(err);
        }));
  }
}
