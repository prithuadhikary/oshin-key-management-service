import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from './ngrx/auth.reducers';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import { FlexLayoutModule } from '@angular/flex-layout';
import {MatButtonModule} from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { AuthEffects } from './ngrx/auth.effects';

const routes: Routes = [
  { path: '', pathMatch: 'full', component: LoginComponent }
]

@NgModule({
  imports: [
    CommonModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    FlexLayoutModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    StoreModule.forFeature(fromAuth.authFeatureKey, fromAuth.reducers),
    EffectsModule.forFeature([AuthEffects])
  ],
  declarations: [
    LoginComponent
  ]
})
export class AuthModule { }
