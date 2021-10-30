import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LoadTrustChainsRequest} from '../model/LoadTrustChainsRequest';
import {LoadTrustChainsResponse} from '../model/LoadTrustChainsResponse';

@Injectable({
  providedIn: 'root'
})
export class TrustChainService {

  constructor(private http: HttpClient) { }

  list(loadTrustChainsRequest: LoadTrustChainsRequest): Observable<LoadTrustChainsResponse> {
    const params = new HttpParams().set('page', loadTrustChainsRequest.page.toString()).set('size', loadTrustChainsRequest.size.toString());
    return this.http.get<LoadTrustChainsResponse>(
      '/api/trust-chain-service/trust-chain',
      { params }
    );
  }

}
