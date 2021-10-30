import { Component, OnInit } from '@angular/core';
import {TrustChainService} from '../../../shared/services/trust-chain.service';
import {faBuilding, faPlus, faEdit, faTrash, faCertificate} from '@fortawesome/free-solid-svg-icons';
import {MatDialog} from '@angular/material/dialog';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {TrustChain} from '../../../shared/model/TrustChain';
import {LoadTrustChainsResponse} from '../../../shared/model/LoadTrustChainsResponse';

@Component({
  selector: 'app-trust-chain-list',
  templateUrl: './trust-chain-list.component.html',
  styleUrls: ['./trust-chain-list.component.scss']
})
export class TrustChainListComponent implements OnInit {

  constructor(
    private trustChainService: TrustChainService,
    public dialog: MatDialog
  ) { }

  faBuilding = faBuilding;
  faPlus = faPlus;
  faEdit = faEdit;
  faTrash = faTrash;
  faCertificate = faCertificate;

  selectedTrustChain: TrustChain;

  trustChainResponse$: Observable<LoadTrustChainsResponse>;

  // Page params
  paginatorLength;
  pageSize = 10;
  currentPageIndex = 0;
  pageSizeOptions: number[] = [5, 10, 25, 100];

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

  setSelectedTenant(trustChain: TrustChain): void {
    this.selectedTrustChain = trustChain;
  }

  isSelected(trustChain: TrustChain): boolean {
    return trustChain.id === this.selectedTrustChain?.id;
  }

  openAddDialog(): void {
    /*this.dialog.open(AddTenantComponent, {
      disableClose: true
    }).afterClosed().subscribe(result => {
      console.log(result);
      this.trustChainService.create(result).subscribe(data => {
        this.loadList(0, 20);
      });
    });*/
  }

  openDeleteDialog(): void {
    /*this.dialog.open(DeleteTenantComponent, { data: { tenantName: this.selectedTrustChain.name }})
      .afterClosed().subscribe(result => {
      if (result) {
        this.trustChainService.delete(this.selectedTrustChain.id)
          .subscribe(() => this.loadList(this.currentPageIndex, this.pageSize));
      }
    });*/
  }

  changePage(event: { pageIndex: number, pageSize: number }): void {
    this.currentPageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.trustChainResponse$ = this.trustChainService.list({ page: event.pageIndex, size: event.pageSize});
  }

  openEditDialog(): void {
    /*this.dialog.open(EditTenantComponent, {
      disableClose: true,
      data: this.selectedTrustChain
    }).afterClosed().subscribe(result => {
      this.trustChainService.update(result).subscribe((data) => {
        Object.assign(this.selectedTrustChain, data);
      });
    });*/
  }
}
