import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../material.module';
import { TenantService } from './services/tenant.service';
import { KeyTypeDisplayPipe } from './pipes/key-type-display.pipe';

@NgModule({
  declarations: [KeyTypeDisplayPipe],
  imports: [
    CommonModule,
    MaterialModule
  ],
  providers: [
    TenantService
  ],
  exports: [
    KeyTypeDisplayPipe,
    KeyTypeDisplayPipe
  ]
})
export class SharedModule { }
