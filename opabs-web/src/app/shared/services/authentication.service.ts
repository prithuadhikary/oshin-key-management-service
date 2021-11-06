import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {OAuthService} from 'angular-oauth2-oidc';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private readonly idTokenKey = 'id_token';
  private readonly accessTokenKey = 'access_token';

  constructor(private oAuthService: OAuthService) {
  }

  public fetchTokens(authorizationCode: string): Promise<{ accessToken: any, idToken: any }> {
    return new Promise(resolve => {
      if (!sessionStorage.getItem(this.idTokenKey)) {
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
          setTimeout(() => this.fetchTokens(authorizationCode), (result.expires_in - 5000) * 1000);
          resolve({idToken: result.id_token, accessToken: result.access_token});
        });
      } else {
        resolve({
          idToken: sessionStorage.getItem(this.idTokenKey),
          accessToken: sessionStorage.getItem(this.accessTokenKey)
        });
      }
    });
  }
}
