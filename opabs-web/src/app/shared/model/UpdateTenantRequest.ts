import {ContactInfo} from './ContactInfo';

export interface UpdateTenantRequest{
  id: string;
  name: string;
  contactInfo: ContactInfo;
}
