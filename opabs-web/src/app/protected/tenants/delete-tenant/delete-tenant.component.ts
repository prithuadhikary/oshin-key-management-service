import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-delete-tenant',
  templateUrl: './delete-tenant.component.html',
  styleUrls: ['./delete-tenant.component.scss']
})
export class DeleteTenantComponent implements OnInit {

  tenantName: string;

  constructor(@Inject(MAT_DIALOG_DATA) private dialogData: { tenantName: string }) {
    this.tenantName = dialogData.tenantName;
  }

  ngOnInit(): void {
  }

}
