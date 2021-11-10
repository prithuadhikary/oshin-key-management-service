import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {empty, Observable} from 'rxjs';
import {AuthenticationService} from '../services/authentication.service';
import {catchError, switchMap} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AccessTokenInterceptorService implements HttpInterceptor {

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const matches = environment.interceptorExclusions.filter(url => {
      return req.url.startsWith(url);
    });
    if (matches.length > 0) {
      return next.handle(req);
    } else {
      return this.authenticationService.fetchTokens(null)
        .pipe(
          switchMap(token => {
          const headers = req.headers
            .set('Authorization', 'Bearer ' + token.accessToken)
            .append('Content-Type', 'application/json');
          const reqClone = req.clone({
            headers
          });
          return next.handle(reqClone).pipe(
            catchError((error) => {
              if (error.status === 401 || error.status === 403) {
                this.router.navigate(['/login']);
              }
              return empty();
            })
          );
        }));
    }
  }
}
