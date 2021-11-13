import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {OAuthService} from 'angular-oauth2-oidc';
import {fromPromise} from 'rxjs/internal-compatibility';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private readonly idTokenKey = 'id_token';
  private readonly accessTokenKey = 'access_token';
  private readonly authCodeKey = 'authorization_code';

  constructor(
    private oAuthService: OAuthService,
    private http: HttpClient,
    private router: Router
  ) {
  }

  public fetchTokens(authorizationCode: string): Observable<{ accessToken: any, idToken: any }> {
    return fromPromise(new Promise((resolve, reject) => {
      const oldCode = sessionStorage.getItem(this.authCodeKey);
      if (authorizationCode != null && authorizationCode !== oldCode) {
        sessionStorage.setItem(this.authCodeKey, authorizationCode);
        const parameters = {
          grant_type: 'authorization_code',
          client_id: environment.okta.clientId,
          redirect_uri: environment.okta.redirectUri,
          code: authorizationCode,
          code_verifier: sessionStorage.getItem('PKCE_verifier')
        };
        this.oAuthService.tokenEndpoint = environment.okta.tokenEndpoint;
        this.oAuthService.fetchTokenUsingGrant('authorization_code', parameters).then(result => {
          sessionStorage.setItem(this.idTokenKey, result.id_token);
          sessionStorage.setItem(this.accessTokenKey, result.access_token);
          resolve({idToken: result.id_token, accessToken: result.access_token});
        }, error => {
          reject(error);
        });
      } else {
        resolve({
          idToken: sessionStorage.getItem(this.idTokenKey),
          accessToken: sessionStorage.getItem(this.accessTokenKey)
        });
      }
    }));
  }

  performLogout(): void {
    const idToken = sessionStorage.getItem('id_token');
    const params = new HttpParams().set('id_token_hint', idToken)
      .set('post_logout_redirect_uri', environment.okta.postLogoutRedirectUri);
    this.oAuthService.revokeTokenAndLogout();
    this.oAuthService.logOut();
    window.location.href = `${environment.okta.logoutEndpoint}?${params.toString()}`;
  }
}
