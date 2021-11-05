import {Component, OnInit} from '@angular/core';
import {CertificateService} from '../../../shared/services/certificate.service';
import {Observable} from 'rxjs';
import {ListResponse} from '../../../shared/model/ListResponse';
import {Certificate} from '../../../shared/model/Certificate';
import {tap} from 'rxjs/operators';
import {faDownload, faTimesCircle} from '@fortawesome/free-solid-svg-icons';
import {saveAs} from 'file-saver';
import {MatDialog} from '@angular/material/dialog';
import {AddCertificateComponent} from '../add-certificate/add-certificate.component';
import {TrustChainService} from '../../../shared/services/trust-chain.service';
import {TrustChain} from '../../../shared/model/TrustChain';
import {Router} from '@angular/router';

@Component({
  selector: 'app-certificates-list',
  templateUrl: './certificates-list.component.html',
  styleUrls: ['./certificates-list.component.scss']
})
export class CertificatesListComponent implements OnInit {

  certificateListResponse$: Observable<ListResponse<Certificate>>;

  selectedCertificate: Certificate;

  trustChain: TrustChain;

  parentCertificate: Certificate;

  // Page params
  paginatorLength;
  pageSize = 10;
  currentPageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  faDownload = faDownload;
  faTimesCircle = faTimesCircle;

  constructor(
    private certificateService: CertificateService,
    private trustChainService: TrustChainService,
    private dialog: MatDialog,
    private router: Router
  ) {
    this.parentCertificate = this.router.getCurrentNavigation()?.extras?.state?.parentCertificate;
  }

  ngOnInit(): void {
      this.loadList(0, 20, this.parentCertificate?.id);
  }

  loadList(page: number, pageSize: number, parentCertificateId: string): void {
    this.certificateListResponse$ = this.certificateService.list({
      page,
      size: pageSize,
      parentCertificateId
    }).pipe(
      tap((data: ListResponse<Certificate>) => {
        this.paginatorLength = data.totalElements;
        this.pageSize = data.pageSize;
      })
    );
  }

  downloadCert(): void {
    this.certificateService.downloadCertificate(this.selectedCertificate.id)
      .subscribe(data => {
        saveAs(data, 'certificate-' + this.selectedCertificate.id + '.crt');
      });
  }

  searchTextChanged(value: string): void {
    this.currentPageIndex = 0;
    this.certificateListResponse$ = this.certificateService.list({
      size: this.pageSize,
      page: this.currentPageIndex,
      search: value,
      parentCertificateId: this.parentCertificate?.id
    });
  }

  clearParentCertificateId(): void {
    this.parentCertificate = null;
    this.loadList(this.currentPageIndex, this.pageSize, null);
  }

  downloadCertChain(): void {
    this.certificateService.downloadCertificateChain(this.selectedCertificate.id)
      .subscribe(data => {
        saveAs(data, 'certificate-chain-' + this.selectedCertificate.id + '.p7b');
      });
  }

  changePage(event: { pageIndex: number, pageSize: number }): void {
    this.currentPageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.certificateListResponse$ = this.certificateService.list({page: event.pageIndex, size: event.pageSize});
  }

  setSelected(certificate: Certificate): void {
    if (this.selectedCertificate?.id !== certificate.id) {
      this.selectedCertificate = certificate;
      this.trustChainService.show(this.selectedCertificate.trustChainId)
        .subscribe(data => {
          this.trustChain = data;
        });
    }
  }

  isSelected(certificate: Certificate): boolean {
    return certificate.id === this.selectedCertificate?.id;
  }

  canCreateChildCertificates(): boolean {
    return this.selectedCertificate.keyUsages.includes('KEY_CERT_SIGN') && this.selectedCertificate.keyUsages.includes('CRL_SIGN');
  }

  openAddCertificate(): void {
    this.dialog.open(AddCertificateComponent, {
      data: { parentKeyType: this.selectedCertificate.keyType, pathLengthConstraint: this.selectedCertificate.pathLengthConstraint }
    }).afterClosed().subscribe(result => {
        if (typeof result === 'object') {
          result.parentCertificateId = this.selectedCertificate.id;
          this.certificateService.create(result).subscribe(() => {
            this.currentPageIndex = 0;
            this.loadList(0, this.pageSize, this.parentCertificate?.id);
          });
        }
    });
  }
}
