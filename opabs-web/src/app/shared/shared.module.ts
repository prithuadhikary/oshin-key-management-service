import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../material.module';
import { TenantService } from './services/tenant.service';
import { KeyTypeDisplayPipe } from './pipes/key-type-display.pipe';
import { CertificateInfoComponent } from './components/certificate-info/certificate-info.component';

@NgModule({
  declarations: [KeyTypeDisplayPipe, CertificateInfoComponent],
  imports: [
    CommonModule,
    MaterialModule
  ],
  providers: [
    TenantService
  ],
  exports: [
    KeyTypeDisplayPipe,
    CertificateInfoComponent
  ]
})
export class SharedModule { }
