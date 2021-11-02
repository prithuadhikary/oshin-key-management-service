import { Component, OnInit} from '@angular/core';
import {TenantService} from '../../../shared/services/tenant.service';
import {Observable} from 'rxjs';
import {LoadTenantsResponse} from '../../../shared/model/LoadTenantsResponse';
import {faBuilding, faPlus, faEdit, faTrash} from '@fortawesome/free-solid-svg-icons';
import {Tenant} from '../../../shared/model/Tenant';
import {CertificateService} from '../../../shared/services/certificate.service';
import {MatDialog} from '@angular/material/dialog';
import {AddTenantComponent} from '../add-tenant/add-tenant.component';
import * as c3 from 'c3';
import {CertificateReportResponseByKeyType} from '../../../shared/model/CertificateReportResponseByKeyType';
import {EditTenantComponent} from '../edit-tenant/edit-tenant.component';
import {tap} from 'rxjs/operators';
import {DeleteTenantComponent} from '../delete-tenant/delete-tenant.component';
import {CertificateReportResponseByHierarchy} from '../../../shared/model/CertificateReportResponseByHierarchy';

@Component({
  selector: 'app-tenants-list',
  templateUrl: './tenants-list.component.html',
  styleUrls: ['./tenants-list.component.scss']
})
export class TenantsListComponent implements OnInit {

  constructor(
    private tenantService: TenantService,
    private certificateService: CertificateService,
    public dialog: MatDialog
  ) { }

  faBuilding = faBuilding;
  faPlus = faPlus;
  faEdit = faEdit;
  faTrash = faTrash;

  selectedTenant: Tenant;

  tenantsResponse$: Observable<LoadTenantsResponse>;

  showChart = false;

  // Page params
  paginatorLength;
  pageSize = 10;
  currentPageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  ngOnInit(): void {
    this.loadList(0, this.pageSize);
  }

  loadList(page: number, pageSize: number): void {
    this.tenantsResponse$ = this.tenantService.list({
      page,
      size: pageSize
    }).pipe(
      tap((data: LoadTenantsResponse) => {
        this.paginatorLength = data.totalElements;
        this.pageSize = data.pageSize;
      })
    );
  }

  setSelectedTenant(tenant: Tenant): void {
    this.selectedTenant = tenant;
    this.certificateService.fetchCertificateCountByTenantId(tenant.id)
      .subscribe((data) => {
        const {chartDataPerKeyType, colorPalette} = this.processDataForKeyType(data);
        this.generateC3Chart(chartDataPerKeyType, data.totalCertificateCount, colorPalette);
      }, (error => {
        console.log(error);
      }));
  }

  private processDataForKeyType(data: CertificateReportResponseByKeyType): { chartDataPerKeyType: Array<string>, colorPalette: object } {
    const chartData: any = [];
    let index = 0;
    const colors = ['#516ee5', '#FFAB40', '#795548'];
    const colorPalette = {};
    for (const certCountInfo of data.certificateCountInfos) {
      const key = certCountInfo.keyType.replace('_', ' ') + ' Certificate(s)';
      chartData.push([
        key,
        certCountInfo.certificateCount
      ]);
      colorPalette[key] = colors[index++];
    }
    return {chartDataPerKeyType: chartData, colorPalette};
  }

  private generateC3Chart(chartData: any, totalCount, colorPalette): void {
    this.showChart = chartData && chartData.length > 0;
    setTimeout(() => {
      c3.generate({
        bindto: '#chart',
        data: {
          columns: chartData,
          type: 'donut',
          colors: colorPalette
        },
        donut: {
          title: 'Total \n' + totalCount + ' Certificate(s)',
          label: {
            format: (value: number): number => {
              return value;
            }
          }
        }
      });
    }, 200);
  }

  isSelected(tenant): boolean {
    return tenant.id === this.selectedTenant?.id;
  }

  openAddDialog(): void {
    this.dialog.open(AddTenantComponent, {
      disableClose: true
    }).afterClosed().subscribe(result => {
        if (typeof result === 'object') {
          this.tenantService.create(result).subscribe(data => {
            this.loadList(0, 20);
          });
        }
    });
  }

  openDeleteDialog(): void {
    this.dialog.open(DeleteTenantComponent, { data: { tenantName: this.selectedTenant.name }})
      .afterClosed().subscribe(result => {
        if (result) {
          this.tenantService.delete(this.selectedTenant.id)
            .subscribe(() => this.loadList(this.currentPageIndex, this.pageSize));
        }
    });
  }

  changePage(event: { pageIndex: number, pageSize: number }): void {
    this.currentPageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.tenantsResponse$ = this.tenantService.list({ page: event.pageIndex, size: event.pageSize});
  }

  openEditDialog(): void {
    this.dialog.open(EditTenantComponent, {
      disableClose: true,
      data: this.selectedTenant
    }).afterClosed().subscribe(result => {
      if (typeof result === 'object') {
        this.tenantService.update(result).subscribe((data) => {
          Object.assign(this.selectedTenant, data);
        });
      }
    });
  }
}
