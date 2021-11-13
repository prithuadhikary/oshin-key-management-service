import {Injectable} from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor() { }

  public userInfo(): { name: string, email: string } {
    const idToken = sessionStorage.getItem('id_token');
    const helper = new JwtHelperService();
    return helper.decodeToken(idToken);
  }

  public groupInfo(): { groups: Array<string> } {
    const accessToken = sessionStorage.getItem('access_token');
    const helper = new JwtHelperService();
    return helper.decodeToken(accessToken);
  }

  public isAdmin(): boolean {
    const groupInfo = this.groupInfo();
    return groupInfo.groups.includes('opabs-admin');
  }

  public isTenantAdmin(): boolean {
    const groupInfo = this.groupInfo();
    return groupInfo.groups.includes('tenant-admin');
  }

}
