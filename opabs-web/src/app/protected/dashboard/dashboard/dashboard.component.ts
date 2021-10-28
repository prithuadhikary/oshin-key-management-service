import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { JwtHelperService } from "@auth0/angular-jwt";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  constructor(
    private oAuthService: OAuthService,
    private route: ActivatedRoute
    ) { }

  ngOnInit(): void {
    this.route.fragment.subscribe(fragment => {
      const fragmentParams = new URLSearchParams(fragment);
      const helper = new JwtHelperService();
      console.log(helper.decodeToken(fragmentParams.get("id_token")));
    });
    const claims = this.oAuthService.getAccessToken();
    console.log(claims);
  }

}
