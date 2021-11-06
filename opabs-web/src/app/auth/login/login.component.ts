import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AuthConfig, JwksValidationHandler, OAuthService } from 'angular-oauth2-oidc';
import { AuthActions } from '../ngrx/auth.actions';
import { AuthState } from '../ngrx/auth.reducers';
import {environment} from '../../../environments/environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  formGroup: FormGroup;

  constructor(
    private store: Store<AuthState>,
    private fb: FormBuilder,
    private oAuthService: OAuthService
  ) {
    this.formGroup = this.fb.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });

    const authCodeFlowConfig: AuthConfig = {
      issuer: environment.okta.issuerUrl,
      redirectUri: environment.okta.redirectUri,
      tokenEndpoint: environment.okta.tokenEndpoint,
      clientId: environment.okta.clientId,
      responseType: 'code',
      scope: environment.okta.scopes,
      requestAccessToken: true
    };

    this.oAuthService.tokenValidationHandler = new JwksValidationHandler();
    this.oAuthService.configure(authCodeFlowConfig);
    this.oAuthService.loadDiscoveryDocument();
  }

  ngOnInit(): void {
  }

  login() {
    this.oAuthService.initCodeFlow();
  }

  get username(): AbstractControl | null | undefined {
    return this.formGroup?.get('username');
  }

  get password(): AbstractControl | null | undefined {
    return this.formGroup?.get('password');
  }

}
