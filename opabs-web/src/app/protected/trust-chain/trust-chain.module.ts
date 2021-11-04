import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TrustChainListComponent } from './trust-chain-list/trust-chain-list.component';
import {RouterModule, Routes} from '@angular/router';
import {FlexModule} from '@angular/flex-layout';
import {MatButtonModule} from '@angular/material/button';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {MatPaginatorModule} from '@angular/material/paginator';
import {SharedModule} from '../../shared/shared.module';
import { AddTrustChainComponent } from './add-trust-chain/add-trust-chain.component';
import {MatDialogModule} from '@angular/material/dialog';
import {ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatSelectModule} from '@angular/material/select';
import {MatTreeModule} from '@angular/material/tree';
import {MatIconModule} from '@angular/material/icon';
import {MatCheckboxModule} from '@angular/material/checkbox';

const routes: Routes = [
  { path: '', component: TrustChainListComponent }
];

@NgModule({
  declarations: [TrustChainListComponent, AddTrustChainComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        FlexModule,
        MatButtonModule,
        FontAwesomeModule,
        MatPaginatorModule,
        SharedModule,
        MatDialogModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatDatepickerModule,
        MatSelectModule,
        MatTreeModule,
        MatIconModule,
        MatCheckboxModule
    ]
})
export class TrustChainModule { }
