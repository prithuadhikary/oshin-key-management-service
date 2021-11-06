import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardComponent } from './dashboard/dashboard.component';
import { RouterModule, Routes } from '@angular/router';
import {FlexModule} from '@angular/flex-layout';

const routes: Routes = [
 { path: '', component: DashboardComponent }
];

@NgModule({
  declarations: [DashboardComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        FlexModule
    ]
})
export class DashboardModule { }
