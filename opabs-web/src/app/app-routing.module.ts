import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthModule } from './auth/auth.module';
import { MaterialModule } from './material.module';
import { ProtectedViewComponent } from './protected/protected-view/protected-view.component';
import { ProtectedModule } from './protected/protected.module';

const routes: Routes = [
  { 
    path: '', 
    loadChildren: () => AuthModule
  },
  { 
    path: 'protected',
    component: ProtectedViewComponent,
    loadChildren: () => ProtectedModule
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes),
    MaterialModule
  ],
  declarations: [ ProtectedViewComponent ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
