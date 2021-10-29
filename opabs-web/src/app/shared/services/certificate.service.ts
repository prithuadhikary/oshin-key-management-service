import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {CertificateReportResponse} from '../model/CertificateReportResponse';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  constructor(private http: HttpClient) {
  }

  fetchCertificateCount(tenantId: string): Observable<CertificateReportResponse> {
    return this.http.get<CertificateReportResponse>('/api/trust-chain-service/certificate-report/' + tenantId);
  }


}
