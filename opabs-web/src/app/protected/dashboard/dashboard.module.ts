import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './dashboard/dashboard.component';
import {RouterModule, Routes} from '@angular/router';
import {FlexModule} from '@angular/flex-layout';
import {MatCardModule} from '@angular/material/card';
import {AccessTokenResolverService} from '../protected-view/resolver/access-token-resolver.service';
import { CertificateIssueDensityComponent } from './charts/certificate-issue-density/certificate-issue-density.component';
import { CertificatesByKeytypeComponent } from './charts/certificates-by-keytype/certificates-by-keytype.component';

const routes: Routes = [
  {
    path: '', component: DashboardComponent,
    resolve: {
      accessTokenResolver: AccessTokenResolverService
    }
  }
];

@NgModule({
  declarations: [DashboardComponent, CertificateIssueDensityComponent, CertificatesByKeytypeComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        FlexModule,
        MatCardModule
    ]
})
export class DashboardModule { }
