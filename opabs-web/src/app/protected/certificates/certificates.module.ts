import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import { CertificatesListComponent } from './certificates-list/certificates-list.component';
import {FlexModule} from '@angular/flex-layout';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {MatPaginatorModule} from '@angular/material/paginator';
import {SharedModule} from '../../shared/shared.module';
import {MatButtonModule} from '@angular/material/button';
import { AddCertificateComponent } from './add-certificate/add-certificate.component';
import {MatDialogModule} from '@angular/material/dialog';
import {ReactiveFormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatDatepickerModule} from '@angular/material/datepicker';

const routes: Routes = [
  { path: '', component: CertificatesListComponent }
];

@NgModule({
  declarations: [CertificatesListComponent, AddCertificateComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FlexModule,
    FontAwesomeModule,
    MatPaginatorModule,
    SharedModule,
    MatButtonModule,
    MatDialogModule,
    ReactiveFormsModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule
  ]
})
export class CertificatesModule { }
