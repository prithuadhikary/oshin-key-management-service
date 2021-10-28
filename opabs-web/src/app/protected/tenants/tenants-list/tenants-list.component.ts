import {Component, OnInit} from '@angular/core';
import {TenantService} from '../../../shared/services/tenant.service';
import {Observable} from 'rxjs';
import {LoadTenantsResponse} from '../../../shared/model/LoadTenantsResponse';
import { faBuilding } from '@fortawesome/free-solid-svg-icons';
import {Tenant} from '../../../shared/model/Tenant';

@Component({
  selector: 'app-tenants-list',
  templateUrl: './tenants-list.component.html',
  styleUrls: ['./tenants-list.component.scss']
})
export class TenantsListComponent implements OnInit {

  faBuilding = faBuilding;

  selectedTenant: Tenant;

  constructor(private tenantService: TenantService) { }

  tenantsResponse: Observable<LoadTenantsResponse>;

  ngOnInit(): void {
    this.tenantsResponse = this.tenantService.list({
      page: 0,
      size: 20
    });
  }

  setSelectedTenant(tenant: Tenant): void {
    this.selectedTenant = tenant;
  }

  isSelected(tenant): boolean {
    return tenant.id === this.selectedTenant?.id;
  }
}
