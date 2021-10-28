import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { AuthConfig, JwksValidationHandler, OAuthService } from 'angular-oauth2-oidc';
import { AuthActions } from '../ngrx/auth.actions';
import { AuthState } from '../ngrx/auth.reducers';

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
      issuer: 'https://dev-52177607.okta.com/oauth2/default',
      redirectUri: 'http://localhost:4200/protected/dashboard/',
      clientId: '0oa238u6a1iZLeox75d7',
      responseType: 'code',
      scope: 'openid profile email',
      showDebugInformation: true,
    };

    this.oAuthService.tokenValidationHandler = new JwksValidationHandler();
    this.oAuthService.configure(authCodeFlowConfig);
    this.oAuthService.loadDiscoveryDocument();
  }

  ngOnInit(): void {
    
  }

  login() {
    // this.store.dispatch(AuthActions.login({
    //   username: this.username?.value,
    //   password: this.password?.value
    // }));
    this.oAuthService.initLoginFlow();
  }

  get username(): AbstractControl | null | undefined {
    return this.formGroup?.get('username');
  }

  get password(): AbstractControl | null | undefined {
    return this.formGroup?.get('password');
  }

}
