import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {OAuthService} from 'angular-oauth2-oidc';
import {AuthenticationService} from '../../shared/services/authentication.service';
import {MenuItemService} from '../../shared/services/menu-item.service';
import {MenuItem} from '../../shared/model/MenuItem';

@Component({
  selector: 'app-protected-view',
  templateUrl: './protected-view.component.html',
  styleUrls: ['./protected-view.component.scss']
})
export class ProtectedViewComponent implements OnInit {

  private readonly accessTokenKey = 'access_token';

  menuItems: Array<MenuItem> = [];

  constructor(
    private oAuthService: OAuthService,
    private router: Router,
    private route: ActivatedRoute,
    private authenticationService: AuthenticationService,
    private menuItemService: MenuItemService
    ) { }

  ngOnInit(): void {
    if (!sessionStorage.getItem(this.accessTokenKey)) {
      this.route.queryParams.subscribe(params => {
        this.authenticationService.fetchTokens(params.code).then(() => {
          this.menuItems = this.menuItemService.menuItems;
        });
      });
    } else {
      this.menuItems = this.menuItemService.menuItems;
    }
  }

  logout(): void {
    this.oAuthService.revokeTokenAndLogout();
    this.oAuthService.logOut();
    this.router.navigate(['/login']);
  }

}
