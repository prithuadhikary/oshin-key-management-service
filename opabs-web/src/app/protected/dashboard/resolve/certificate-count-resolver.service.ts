import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {CertificateService} from '../../../shared/services/certificate.service';
import {Observable} from 'rxjs';
import {delay} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CertificateCountResolver implements Resolve<{ total: number }>{

  constructor(private certificateService: CertificateService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<{ total: number }> {
    return this.certificateService.fetchCertificateCount().pipe(delay(2000));
  }

}
