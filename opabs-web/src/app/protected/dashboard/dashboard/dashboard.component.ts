import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import { faBuilding, faLink, faFileContract } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  faBuilding = faBuilding;
  faLink = faLink;
  faFileContract = faFileContract;

  constructor(private route: ActivatedRoute) { }

  totalCertificateCount: number;
  totalTrustChainCount: number;
  totalTenantCount: number;

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.totalCertificateCount = data.accessTokenResolver.totalCertificates.total;
      this.totalTrustChainCount = data.accessTokenResolver.totalTrustChains.total;
      this.totalTenantCount = data.accessTokenResolver.totalTenants.total;
    });
  }
}
