import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../material.module';
import { TenantService } from './services/tenant.service';
import { KeyTypeDisplayPipe } from './pipes/key-type-display.pipe';
import { CertificateInfoComponent } from './components/certificate-info/certificate-info.component';
import { LoaderComponent } from './components/loader/loader.component';
import {MatProgressBarModule} from '@angular/material/progress-bar';

@NgModule({
  declarations: [KeyTypeDisplayPipe, CertificateInfoComponent, LoaderComponent],
  imports: [
    CommonModule,
    MaterialModule,
    MatProgressBarModule
  ],
  providers: [
    TenantService
  ],
  exports: [
    KeyTypeDisplayPipe,
    CertificateInfoComponent,
    LoaderComponent
  ]
})
export class SharedModule { }
