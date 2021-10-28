import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { MatCardModule } from '@angular/material/card';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatDialogModule} from '@angular/material/dialog';
import {MatInputModule} from '@angular/material/input';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatMenuModule} from '@angular/material/menu';
import {MatSelectModule} from '@angular/material/select';
import {MatDatepickerModule} from '@angular/material/datepicker';
import { MatMomentDateModule } from '@angular/material-moment-adapter';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';

@NgModule({
  declarations: [],
  imports: [ CommonModule ],
  exports: [
    MatToolbarModule,
    MatButtonModule,
    MatCardModule,
    MatGridListModule,
    MatIconModule,
    MatDialogModule,
    MatInputModule,
    MatSnackBarModule,
    MatMenuModule,
    MatSelectModule,
    MatDatepickerModule,
    MatMomentDateModule,
    FlexLayoutModule,
    FontAwesomeModule,
    InfiniteScrollModule
  ]
})
export class MaterialModule { }
