import {Component, OnInit} from '@angular/core';
import {TrustChainService} from '../../../shared/services/trust-chain.service';
import {faCertificate, faDownload} from '@fortawesome/free-solid-svg-icons';
import {MatDialog} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {TrustChain} from '../../../shared/model/TrustChain';
import {LoadTrustChainsResponse} from '../../../shared/model/LoadTrustChainsResponse';
import {CertificateService} from '../../../shared/services/certificate.service';
import {CertificateReportResponseByKeyType} from '../../../shared/model/CertificateReportResponseByKeyType';
import * as c3 from 'c3';
import {CertificateReportResponseByHierarchy} from '../../../shared/model/CertificateReportResponseByHierarchy';
import {AddTrustChainComponent} from '../add-trust-chain/add-trust-chain.component';
import { saveAs } from 'file-saver';
import {Router} from '@angular/router';

@Component({
  selector: 'app-trust-chain-list',
  templateUrl: './trust-chain-list.component.html',
  styleUrls: ['./trust-chain-list.component.scss']
})
export class TrustChainListComponent implements OnInit {

  constructor(
    private trustChainService: TrustChainService,
    private certificateService: CertificateService,
    private dialog: MatDialog,
    private router: Router
  ) {
  }

  faCertificate = faCertificate;
  faDownload = faDownload;

  selectedTrustChain: TrustChain;

  trustChainResponse$: Observable<LoadTrustChainsResponse>;

  // Page params
  paginatorLength;
  pageSize = 10;
  currentPageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  showChart = false;

  ngOnInit(): void {
    this.loadList(0, this.pageSize);
  }

  loadList(page: number, pageSize: number): void {
    this.trustChainResponse$ = this.trustChainService.list({
      page,
      size: pageSize
    }).pipe(
      tap((data: LoadTrustChainsResponse) => {
        this.paginatorLength = data.totalElements;
        this.pageSize = data.pageSize;
      })
    );
  }

  setSelected(trustChain: TrustChain): void {
    this.selectedTrustChain = trustChain;
    this.certificateService.fetchCertificateCountByTrustChainId(trustChain.id)
      .subscribe((data) => {
        const {chartDataPerKeyType, colorPalette} = this.processData(data);
        this.generateC3Chart(chartDataPerKeyType, data.totalCertificateCount, colorPalette, '#chart-by-trust-chain');
      }, (error => {
        console.log(error);
      }));
    this.certificateService.fetchCertificateCountByHierarchy(trustChain.id)
      .subscribe(data => {
        const {chartDataPerKeyType, colorPalette} = this.processDataForHierarchy(data);
        this.generateC3Chart(chartDataPerKeyType, data.totalCount, colorPalette, '#chart-by-hierarchy');
      });
  }


  // tslint:disable-next-line:max-line-length
  private processDataForHierarchy(data: CertificateReportResponseByHierarchy): { chartDataPerKeyType: Array<string>, colorPalette: object } {
    const chartData: any = [];
    let index = 0;
    const colors = ['#516ee5', '#FFAB40', '#795548'];
    const colorPalette = {};
    data.countsByLevel.forEach(({level, count}) => {
      chartData.push([
        level,
        count
      ]);
      colorPalette[level] = colors[index++];
    });
    return {chartDataPerKeyType: chartData, colorPalette};
  }


  private processData(data: CertificateReportResponseByKeyType): { chartDataPerKeyType: Array<string>, colorPalette: object } {
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

  private generateC3Chart(chartData: any, totalCount, colorPalette, chartDivSelector: string): void {
    this.showChart = chartData && chartData.length > 0;
    setTimeout(() => {
      c3.generate({
        bindto: chartDivSelector,
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

  downloadRootCert(): void {
    this.certificateService.downloadCertificate(this.selectedTrustChain.rootCertificate.id)
      .subscribe(data => {
        saveAs(data, 'root_certificate_' + this.selectedTrustChain.name + '.crt');
      });
  }

  isSelected(trustChain: TrustChain): boolean {
    return trustChain.id === this.selectedTrustChain?.id;
  }

  openAddDialog(): void {
    this.dialog.open(AddTrustChainComponent, {
      disableClose: true
    }).afterClosed().subscribe(result => {
      if (typeof result === 'object') {
        this.trustChainService.create(result).subscribe(data => {
          this.loadList(0, 20);
        });
      }
    });
  }

  viewChildCertificates(): void {
    this.router.navigate(['protected', 'certificates'], { state: { parentCertificate: this.selectedTrustChain.rootCertificate }});
  }

  changePage(event: { pageIndex: number, pageSize: number }): void {
    this.currentPageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.trustChainResponse$ = this.trustChainService.list({page: event.pageIndex, size: event.pageSize});
  }
}
