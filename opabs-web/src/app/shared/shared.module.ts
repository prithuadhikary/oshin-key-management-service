import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../material.module';
import { TenantService } from './services/tenant.service';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MaterialModule
  ],
  providers: [
    TenantService
  ]
})
export class SharedModule { }
