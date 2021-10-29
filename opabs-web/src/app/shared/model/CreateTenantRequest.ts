import {ContactInfo} from './ContactInfo';

export interface CreateTenantRequest {
  name: string;
  contactInfo: ContactInfo;
}
