import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {LoadTenantsRequest} from '../model/LoadTenantsRequest';
import {CreateTenantRequest} from '../model/CreateTenantRequest';
import {UpdateTenantRequest} from '../model/UpdateTenantRequest';

@Injectable({
  providedIn: 'root'
})
export class TenantService {

  constructor(private http: HttpClient) { }

  list(loadTenantsRequest: LoadTenantsRequest): Observable<any> {
    const params = new HttpParams().set('page', loadTenantsRequest.page.toString()).set('size', loadTenantsRequest.size.toString());
    return this.http.get(
      '/api/tenant-management-service/tenant',
      { params }
    );
  }

  create(createTenantRequest: CreateTenantRequest): Observable<any> {
    return this.http.post(
      '/api/tenant-management-service/tenant',
      createTenantRequest
    );
  }

  update(updateTenantReqeust: UpdateTenantRequest): Observable<any> {
    return this.http.put(
      '/api/tenant-management-service/tenant/' + updateTenantReqeust.id,
      updateTenantReqeust
    );
  }

  show(id: string): Observable<any> {
    return this.http.get(
      '/api/tenant-management-service/tenant/' + id);
  }

  delete(id: string): Observable<any> {
    return this.http.delete(
      '/api/tenant-management-service/tenant/' + id
    );
  }
}
