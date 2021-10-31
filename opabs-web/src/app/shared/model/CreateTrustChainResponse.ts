import {Certificate} from './Certificate';

export interface CreateTrustChainResponse {
  id: string;
  name: string;
  description: string;
  deleted: string;
  rootCertificate: Certificate;
  tenantExtId: string;
  dateCreated: string;
  dateUpdated: string;
}
