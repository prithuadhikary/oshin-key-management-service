import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthModule } from './auth/auth.module';
import { MaterialModule } from './material.module';
import { ProtectedViewComponent } from './protected/protected-view/protected-view.component';
import { ProtectedModule } from './protected/protected.module';
import {SharedModule} from './shared/shared.module';
import {CommonModule} from '@angular/common';

const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => AuthModule
  },
  {
    path: 'protected',
    component: ProtectedViewComponent,
    loadChildren: () => ProtectedModule
  },
  {
    path: '**',
    loadChildren: () => AuthModule
  }
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes),
        MaterialModule,
        SharedModule,
        CommonModule
    ],
  declarations: [ ProtectedViewComponent ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
