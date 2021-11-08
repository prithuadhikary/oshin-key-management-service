import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {AuthenticationService} from '../../../shared/services/authentication.service';
import {flatMap} from 'rxjs/operators';
import {forkJoin, Observable, of} from 'rxjs';
import {CertificateService} from '../../../shared/services/certificate.service';
import {TrustChainService} from '../../../shared/services/trust-chain.service';
import {TenantService} from '../../../shared/services/tenant.service';

@Injectable({
  providedIn: 'root'
})
export class AccessTokenResolverService implements
  Resolve<{ token: { accessToken: string, idToken: string },
    totalCertificates: { total: number }, totalTrustChains: { total: number }, totalTenants: { total: number }  }>{
  constructor(
    private authenticationService: AuthenticationService,
    private certificateService: CertificateService,
    private trustChainService: TrustChainService,
    private tenantService: TenantService
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot)
    : Observable<{ token: { accessToken: string, idToken: string },
    totalCertificates: { total: number }, totalTrustChains: { total: number }, totalTenants: { total: number } }> {
    return this.authenticationService.fetchTokens(route.queryParams.code)
      .pipe(
        flatMap(value =>
          forkJoin({
            token: of(value),
            totalTenants: this.tenantService.fetchTenantCount(),
            totalCertificates: this.certificateService.fetchCertificateCount(),
            totalTrustChains: this.trustChainService.fetchTrustChainCount()
          })
        )
      );
  }

}
