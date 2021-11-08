import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {CertificateService} from '../../../shared/services/certificate.service';
import {Observable} from 'rxjs';
import {TrustChainService} from '../../../shared/services/trust-chain.service';
import {delay} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TrustChainCountResolver implements Resolve<{ total: number }>{

  constructor(private trustChainService: TrustChainService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<{ total: number }> {
    return this.trustChainService.fetchTrustChainCount().pipe(delay(2000));
  }

}
