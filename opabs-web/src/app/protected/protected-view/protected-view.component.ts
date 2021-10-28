import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-protected-view',
  templateUrl: './protected-view.component.html',
  styleUrls: ['./protected-view.component.scss']
})
export class ProtectedViewComponent implements OnInit {

  constructor(
    private oAuthService: OAuthService,
    private router: Router
    ) { }

  ngOnInit(): void {
  }

  logout() {
    this.oAuthService.revokeTokenAndLogout();
    this.oAuthService.logOut();
    this.router.navigate(['/login']);
  }

}
