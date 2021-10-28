import {Tenant} from './Tenant';

export interface LoadTenantsResponse {
  content: Array<Tenant>;
  page: number;
  pageSize: number;
  totalPages: number;
}
