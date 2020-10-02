import {Injectable, Injector} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {Log} from '../common/utils/log';

// simple static mock responses for urls
const urls = [
  {
    url: '/api/user',
    json: {}
  }
];

// Name has to stay AuthenticationInterceptor to not break imports via filereplacement
@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {
  constructor(private injector: Injector) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    for (const element of urls) {
      if (request.url.endsWith(element.url)) {
        Log.info('Loaded from json : ' + request.url);
        return of(new HttpResponse({status: 200, body: ((element.json) as any).default}));
      }
    }
    Log.info('Loaded from http call :' + request.url);
    return next.handle(request);
  }
}
