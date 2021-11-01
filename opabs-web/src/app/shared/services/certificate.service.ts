import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {CertificateReportResponseByKeyType} from '../model/CertificateReportResponseByKeyType';
import {CertificateReportResponseByHierarchy} from '../model/CertificateReportResponseByHierarchy';
import {ListResponse} from '../model/ListResponse';
import {Certificate} from '../model/Certificate';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  constructor(private http: HttpClient) {
  }

  fetchCertificateCountByTenantId(tenantId: string): Observable<CertificateReportResponseByKeyType> {
    return this.http.get<CertificateReportResponseByKeyType>('/api/trust-chain-service/certificate-report/by-tenant/' + tenantId);
  }

  fetchCertificateCountByTrustChainId(trustChainId: string): Observable<CertificateReportResponseByKeyType> {
    return this.http.get<CertificateReportResponseByKeyType>('/api/trust-chain-service/certificate-report/by-trust-chain/' + trustChainId);
  }

  fetchCertificateCountByHierarchy(trustChainId: string): Observable<CertificateReportResponseByHierarchy> {
    return this.http.get<CertificateReportResponseByHierarchy>('/api/trust-chain-service/certificate-report/by-hierarchy/' + trustChainId);
  }

  downloadCertificate(id: string): Observable<Blob> {
      return this.http.get('/api/trust-chain-service/certificate/download/' + id, { responseType: 'blob'});
  }

  list(param: { size: number; page: number }): Observable<ListResponse<Certificate>> {
    const params = new HttpParams().set('page', param.page.toString()).set('size', param.size.toString());
    return this.http.get<ListResponse<Certificate>>('/api/trust-chain-service/certificate', { params });
  }

  create(request: any): Observable<Certificate> {
    return this.http.post<Certificate>('/api/trust-chain-service/certificate', request);
  }
}
