import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthenticationService} from './authentication.service';

@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {
  constructor(private authenticationService: AuthenticationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authReq: HttpRequest<any>;
    if (!!this.authenticationService.currentUsername) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Basic ${btoa(this.authenticationService.currentUsername + ':')}`
        }
      });
    } else {
      authReq = req;
    }

    return next.handle(authReq.clone({
      headers: authReq.headers.append('X-Requested-With', 'XMLHttpRequest')
    }));
  }
}
