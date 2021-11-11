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
    const userIdentity = helper.decodeToken(idToken);
    console.log(userIdentity);
    return userIdentity;
  }

}
