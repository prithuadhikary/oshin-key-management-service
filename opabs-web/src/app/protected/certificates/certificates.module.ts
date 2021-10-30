import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import { CertificatesListComponent } from './certificates-list/certificates-list.component';
import {FlexModule} from '@angular/flex-layout';

const routes: Routes = [
  { path: '', component: CertificatesListComponent }
];

@NgModule({
  declarations: [CertificatesListComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FlexModule
  ]
})
export class CertificatesModule { }
