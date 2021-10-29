import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {LoadTenantsRequest} from '../model/LoadTenantsRequest';
import {CreateTenantRequest} from '../model/CreateTenantRequest';

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

  create(createTenantRequest: any): Observable<any> {
    console.log(createTenantRequest);
    return this.http.post(
      '/api/tenant-management-service/tenant',
      createTenantRequest
    );
  }

}
