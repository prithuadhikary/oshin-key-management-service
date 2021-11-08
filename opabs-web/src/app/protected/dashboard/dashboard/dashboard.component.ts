import {Component, OnInit} from '@angular/core';
import {CertificateService} from '../../../shared/services/certificate.service';
import {TrustChainService} from '../../../shared/services/trust-chain.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  constructor(private route: ActivatedRoute) { }

  totalCertificateCount: number;
  totalTrustChainCount: number;

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.totalCertificateCount = data.accessTokenResolver.totalCertificates.total;
      this.totalTrustChainCount = data.accessTokenResolver.totalTrustChains.total;
    });
  }
}
