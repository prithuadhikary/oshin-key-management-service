import {Certificate} from './Certificate';

export interface TrustChain {

  id: string;
  name: string;
  description: string;
  deleted: boolean;
  rootCertificate: Certificate;
  tenantExtId: string;
  dateCreated: string;
  dateUpdated: string;

}
