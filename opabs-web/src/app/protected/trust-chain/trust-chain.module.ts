import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TrustChainListComponent } from './trust-chain-list/trust-chain-list.component';
import {RouterModule, Routes} from '@angular/router';
import {FlexModule} from '@angular/flex-layout';
import {MatButtonModule} from '@angular/material/button';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {MatPaginatorModule} from '@angular/material/paginator';
import {SharedModule} from '../../shared/shared.module';

const routes: Routes = [
  { path: '', component: TrustChainListComponent }
];

@NgModule({
  declarations: [TrustChainListComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FlexModule,
    MatButtonModule,
    FontAwesomeModule,
    MatPaginatorModule,
    SharedModule
  ]
})
export class TrustChainModule { }
