import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {from, Observable} from 'rxjs';
import {AuthenticationService} from '../services/authentication.service';
import {switchMap} from 'rxjs/operators';
import {fromPromise} from 'rxjs/internal-compatibility';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AccessTokenInterceptorService implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const matches = environment.interceptorExclusions.filter(url => {
      return req.url.startsWith(url);
    });
    if (matches.length > 0) {
      return next.handle(req);
    } else {
      return fromPromise(this.authenticationService.fetchTokens(null))
        .pipe(switchMap(token => {
          const headers = req.headers
            .set('Authorization', 'Bearer ' + token.accessToken)
            .append('Content-Type', 'application/json');
          const reqClone = req.clone({
            headers
          });
          return next.handle(reqClone);
        }));
    }
  }
}
