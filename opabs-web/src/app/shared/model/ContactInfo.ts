import {Address} from './Address';

export interface ContactInfo {

  id: string;
  phone: string;
  emailAddress: string;
  addresses: Array<Address>;

}
