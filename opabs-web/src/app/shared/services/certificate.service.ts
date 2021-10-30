import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {CertificateReportResponseByKeyType} from '../model/CertificateReportResponseByKeyType';
import {CertificateReportResponseByHierarchy} from '../model/CertificateReportResponseByHierarchy';

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


}
