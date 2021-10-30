import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { DashboardModule } from './dashboard/dashboard.module';
import {TenantsModule} from './tenants/tenants.module';
import {CertificatesModule} from './certificates/certificates.module';
import { TrustChainModule } from './trust-chain/trust-chain.module';

const routes: Routes = [
  { path: 'dashboard',  loadChildren: () => DashboardModule },
  { path: 'tenants', loadChildren: () => TenantsModule },
  { path: 'certificates', loadChildren: () => CertificatesModule },
  { path: 'trust-chain', loadChildren: () => TrustChainModule }
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    TrustChainModule
  ],
  declarations: []
})
export class ProtectedModule { }
