import {Component, OnInit} from '@angular/core';
import {TenantService} from '../../../shared/services/tenant.service';
import {Observable} from 'rxjs';
import {LoadTenantsResponse} from '../../../shared/model/LoadTenantsResponse';
import {faBuilding} from '@fortawesome/free-solid-svg-icons';
import {Tenant} from '../../../shared/model/Tenant';
import {Color, ScaleType} from '@swimlane/ngx-charts';
import {CertificateService} from '../../../shared/services/certificate.service';

@Component({
  selector: 'app-tenants-list',
  templateUrl: './tenants-list.component.html',
  styleUrls: ['./tenants-list.component.scss']
})
export class TenantsListComponent implements OnInit {

  chartData = null;
  view: [number, number] = [400, 400];

  // options
  gradient = true;
  showLegend = false;
  showLabels = true;
  isDoughnut = true;

  colorScheme: Color = {
    name: 'colorScheme',
    domain: [ '#00796B', '#FFAB40', '#795548'],
    selectable: true,
    group: ScaleType.Linear
  };

  faBuilding = faBuilding;

  selectedTenant: Tenant;

  constructor(
    private tenantService: TenantService,
    private certificateService: CertificateService
  ) { }

  tenantsResponse: Observable<LoadTenantsResponse>;

  ngOnInit(): void {
    this.tenantsResponse = this.tenantService.list({
      page: 0,
      size: 20
    });
  }

  setSelectedTenant(tenant: Tenant): void {
    this.selectedTenant = tenant;
    this.certificateService.fetchCertificateCount(tenant.id)
      .subscribe((data) => {
        const chartData: any = [];
        for (const certCountInfo of data.certificateCountInfos) {
          chartData.push({
            name: certCountInfo.keyType,
            value: certCountInfo.certificateCount
          });
        }
        this.chartData = chartData;
      });
  }

  isSelected(tenant): boolean {
    return tenant.id === this.selectedTenant?.id;
  }
}
