import { Injectable } from '@angular/core';
import {MenuItems} from '../model/MenuItems';
import {MenuItem} from '../model/MenuItem';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class MenuItemService {

  private _menuItems: Array<MenuItem> = [];

  private readonly menuItemsForGroups: Array<MenuItems> = [
    {
      groupName: 'opabs-admin',
      menuItems: [
        {
          link: ['/protected', 'dashboard'],
          displayName: 'Dashboard',
          routerLinkActiveClass: 'active'
        },
        {
          link: ['/protected', 'tenants'],
          displayName: 'Tenants',
          routerLinkActiveClass: 'active'
        },
        {
          link: ['/protected', 'trust-chain'],
          displayName: 'Trust Chains',
          routerLinkActiveClass: 'active'
        },
        {
          link: ['/protected', 'certificates'],
          displayName: 'Certificates',
          routerLinkActiveClass: 'active'
        }
      ]
    },
    {
      groupName: 'tenant-admin',
      menuItems: [
        {
          link: ['/protected', 'dashboard'],
          displayName: 'Dashboard',
          routerLinkActiveClass: 'active'
        },
        {
          link: ['/protected', 'trust-chain'],
          displayName: 'Trust Chains',
          routerLinkActiveClass: 'active'
        },
        {
          link: ['/protected', 'certificates'],
          displayName: 'Certificates',
          routerLinkActiveClass: 'active'
        }
      ]
    }
  ];

  private jwtHelper = new JwtHelperService();

  constructor() { }

  public setMenuItems(groupNames: Array<string>): void {
    const menuItems: Array<MenuItem> = [];
    groupNames.map(groupName => {
      const menuItemsObj = this.menuItemsForGroups.find(value => value.groupName === groupName);
      if (typeof menuItemsObj !== 'undefined') {
        menuItems.push(...menuItemsObj.menuItems);
      }
    });
    this._menuItems = menuItems;
  }

  public get menuItems(): Array<MenuItem> {
    const token: { groups: Array<string> } = this.jwtHelper.decodeToken(sessionStorage.getItem('access_token'));
    this.setMenuItems(token.groups);
    return this._menuItems;
  }

}
