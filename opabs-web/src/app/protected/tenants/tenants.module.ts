import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TenantsListComponent } from './tenants-list/tenants-list.component';
import {RouterModule, Routes} from '@angular/router';
import {FlexModule} from '@angular/flex-layout';
import {MatCardModule} from '@angular/material/card';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {NgxChartsModule} from '@swimlane/ngx-charts';
const routes: Routes = [
  { path: '', component: TenantsListComponent, pathMatch: 'full' }
];

@NgModule({
  declarations: [TenantsListComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FlexModule,
    MatCardModule,
    FontAwesomeModule,
    NgxChartsModule
  ]
})
export class TenantsModule { }
