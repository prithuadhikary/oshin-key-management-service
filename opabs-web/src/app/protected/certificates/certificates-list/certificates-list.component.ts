import {Component, OnInit} from '@angular/core';
import {CertificateService} from '../../../shared/services/certificate.service';
import {Observable} from 'rxjs';
import {ListResponse} from '../../../shared/model/ListResponse';
import {Certificate} from '../../../shared/model/Certificate';
import {tap} from 'rxjs/operators';
import { faDownload } from '@fortawesome/free-solid-svg-icons';
import { saveAs } from 'file-saver';
import {MatDialog} from '@angular/material/dialog';
import {AddCertificateComponent} from '../add-certificate/add-certificate.component';

@Component({
  selector: 'app-certificates-list',
  templateUrl: './certificates-list.component.html',
  styleUrls: ['./certificates-list.component.scss']
})
export class CertificatesListComponent implements OnInit {

  certificateListResponse$: Observable<ListResponse<Certificate>>;

  selectedCertificate: Certificate;

  // Page params
  paginatorLength;
  pageSize = 10;
  currentPageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  faDownload = faDownload;

  constructor(
    private certificateService: CertificateService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.loadList(0, 20);
  }

  loadList(page: number, pageSize: number): void {
    this.certificateListResponse$ = this.certificateService.list({
      page,
      size: pageSize
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
        saveAs(data, 'certificate.crt');
      });
  }

  changePage(event: { pageIndex: number, pageSize: number }): void {
    this.currentPageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.certificateListResponse$ = this.certificateService.list({page: event.pageIndex, size: event.pageSize});
  }

  setSelected(certificate: Certificate): void {
    this.selectedCertificate = certificate;
  }

  isSelected(certificate: Certificate): boolean {
    return certificate.id === this.selectedCertificate?.id;
  }

  openAddCertificate(): void {
    this.dialog.open(AddCertificateComponent)
      .afterClosed().subscribe(result => {
        result.parentCertificateId = this.selectedCertificate.id;
        this.certificateService.create(result).subscribe(() => {
         this.loadList(0, this.pageSize);
       });
    });
  }
}
