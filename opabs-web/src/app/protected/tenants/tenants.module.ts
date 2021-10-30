import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TenantsListComponent } from './tenants-list/tenants-list.component';
import {RouterModule, Routes} from '@angular/router';
import {FlexModule} from '@angular/flex-layout';
import {MatCardModule} from '@angular/material/card';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatDialogModule} from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';
import { AddTenantComponent } from './add-tenant/add-tenant.component';
import {MatInputModule} from '@angular/material/input';
import { EditTenantComponent } from './edit-tenant/edit-tenant.component';
import {MatPaginatorModule} from '@angular/material/paginator';
import { DeleteTenantComponent } from './delete-tenant/delete-tenant.component';

const routes: Routes = [
  { path: '', component: TenantsListComponent, pathMatch: 'full' }
];

@NgModule({
  declarations: [TenantsListComponent, AddTenantComponent, EditTenantComponent, DeleteTenantComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FlexModule,
    MatCardModule,
    FontAwesomeModule,
    NgxChartsModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    ReactiveFormsModule,
    MatInputModule,
    MatPaginatorModule
  ]
})
export class TenantsModule { }
