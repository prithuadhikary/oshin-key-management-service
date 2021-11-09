import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from '@angular/router';
import {AuthenticationService} from '../../../shared/services/authentication.service';
import {catchError, flatMap} from 'rxjs/operators';
import {empty, forkJoin, Observable, of} from 'rxjs';
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
    private tenantService: TenantService,
    private router: Router
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot)
    : Observable<{ token: { accessToken: string, idToken: string },
    totalCertificates: { total: number }, totalTrustChains: { total: number }, totalTenants: { total: number } }> {
    return this.authenticationService.fetchTokens(route.queryParams.code)
      .pipe(
        catchError((): Observable<any> => {
          sessionStorage.clear();
          this.router.navigate(['login']);
          return empty();
        }),
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
