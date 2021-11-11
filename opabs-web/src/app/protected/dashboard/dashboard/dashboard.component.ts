import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import { faBuilding, faLink, faFileContract } from '@fortawesome/free-solid-svg-icons';
import {UserService} from '../../../shared/services/user.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  faBuilding = faBuilding;
  faLink = faLink;
  faFileContract = faFileContract;
  userDetails: { name: string, email: string };

  constructor(
    private route: ActivatedRoute,
    private userService: UserService
  ) { }

  totalCertificateCount: number;
  totalTrustChainCount: number;
  totalTenantCount: number;

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.totalCertificateCount = data.accessTokenResolver.totalCertificates.total;
      this.totalTrustChainCount = data.accessTokenResolver.totalTrustChains.total;
      this.totalTenantCount = data.accessTokenResolver.totalTenants.total;
      this.userDetails = this.userService.userInfo();
    });
  }
}
