import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { DashboardModule } from './dashboard/dashboard.module';
import {TenantsModule} from './tenants/tenants.module';

const routes: Routes = [
  { path: 'dashboard',  loadChildren: () => DashboardModule },
  { path: 'tenants', loadChildren: () => TenantsModule }
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ],
  declarations: []
})
export class ProtectedModule { }
