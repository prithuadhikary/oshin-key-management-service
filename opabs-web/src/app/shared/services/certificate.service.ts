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
      return this.http.get('/api/trust-chain-service/certificate/download/der/' + id, { responseType: 'blob'});
  }

  downloadCertificateChain(id: string): Observable<Blob> {
      return this.http.get('/api/trust-chain-service/certificate/download/p7b/' + id, { responseType: 'blob'});
  }

  list(param: { size: number; page: number, search?: string, parentCertificateId?: string }): Observable<ListResponse<Certificate>> {
    let params = new HttpParams().set('page', param.page.toString()).set('size', param.size.toString());
    if (param.search) {
      params = params.set('search', param.search);
    }
    if (param.parentCertificateId) {
      params = params.set('parentCertificateId', param.parentCertificateId);
    }
    return this.http.get<ListResponse<Certificate>>('/api/trust-chain-service/certificate', { params });
  }

  create(request: any): Observable<Certificate> {
    return this.http.post<Certificate>('/api/trust-chain-service/certificate', request);
  }

  fetchCertificateCount(): Observable<{ total: number }> {
    return this.http.get<{ total: number}>('/api/trust-chain-service/certificate-report/total');
  }

  fetchIssuedCountByDate(startDate: any, endDate: any): Observable<Array<{ month: string, count: number }>> {
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<Array<{ month: string, count: number }>>('/api/trust-chain-service/certificate-report/count-by-month', { params });
  }
}
