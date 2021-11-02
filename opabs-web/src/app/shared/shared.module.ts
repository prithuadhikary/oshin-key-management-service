import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../material.module';
import { TenantService } from './services/tenant.service';
import { KeyTypeDisplayPipe } from './pipes/key-type-display.pipe';
import { CertificateInfoComponent } from './components/certificate-info/certificate-info.component';
import { LoaderComponent } from './components/loader/loader.component';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import { SearchComponent } from './components/search/search.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

@NgModule({
  declarations: [KeyTypeDisplayPipe, CertificateInfoComponent, LoaderComponent, SearchComponent],
    imports: [
        CommonModule,
        MaterialModule,
        MatProgressBarModule,
        ReactiveFormsModule
    ],
  providers: [
    TenantService
  ],
    exports: [
        KeyTypeDisplayPipe,
        CertificateInfoComponent,
        LoaderComponent,
        SearchComponent
    ]
})
export class SharedModule { }
