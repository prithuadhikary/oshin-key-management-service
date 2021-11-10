import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from '@angular/router';
import {AuthenticationService} from '../../../shared/services/authentication.service';
import {catchError, flatMap} from 'rxjs/operators';
import {empty, forkJoin, Observable, of} from 'rxjs';
import {CertificateService} from '../../../shared/services/certificate.service';
import {TrustChainService} from '../../../shared/services/trust-chain.service';
import {TenantService} from '../../../shared/services/tenant.service';

type ResolvedValues = { token: Observable<{ accessToken: string, idToken: string }>,
  totalCertificates: Observable<{ total: number }>,
  totalTrustChains: Observable<{ total: number }>, totalTenants?: Observable<{ total: number }>  };

@Injectable({
  providedIn: 'root'
})
export class AccessTokenResolverService implements
  Resolve<ResolvedValues>{

  constructor(
    private authenticationService: AuthenticationService,
    private certificateService: CertificateService,
    private trustChainService: TrustChainService,
    private tenantService: TenantService,
    private router: Router
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot)
    : Observable<ResolvedValues> {
    return this.authenticationService.fetchTokens(route.queryParams.code)
      .pipe(
        flatMap((value: { accessToken: string, idToken: string }) => {
          const sourcesObject: ResolvedValues = {
            token: of(value),
            totalCertificates: this.certificateService.fetchCertificateCount(),
            totalTrustChains: this.trustChainService.fetchTrustChainCount()
          };
          sourcesObject.totalTenants = this.tenantService.fetchTenantCount();
          return forkJoin(sourcesObject);
          }
        )
      );
  }

}
