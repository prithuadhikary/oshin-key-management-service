import {ContactInfo} from './ContactInfo';

export interface Tenant {
  id: string;
  name: string;
  deleted: boolean;
  contactInfo: ContactInfo;
}
