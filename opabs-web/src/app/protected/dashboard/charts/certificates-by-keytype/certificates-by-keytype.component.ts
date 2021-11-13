import { Component, OnInit } from '@angular/core';
import {ChartUtilService} from '../../../../shared/services/chart-util.service';
import {CertificateService} from '../../../../shared/services/certificate.service';

@Component({
  selector: 'app-certificates-by-keytype',
  templateUrl: './certificates-by-keytype.component.html',
  styleUrls: ['./certificates-by-keytype.component.scss']
})
export class CertificatesByKeytypeComponent implements OnInit {

  constructor(
    private chartUtilService: ChartUtilService,
    private certificateService: CertificateService
  ) { }

  ngOnInit(): void {
    this.certificateService.fetchCertificateCountByKeyType().subscribe(data => {
      const canvas = document.getElementById('chart-certs-by-key-type') as HTMLCanvasElement;
      const rsaCount: Array<any> = data.certificateCountInfos.filter(countInfo => countInfo.keyType === 'RSA');
      const ecCount: Array<any> = data.certificateCountInfos.filter(countInfo => countInfo.keyType === 'ELLIPTIC_CURVE');
      const ecCertCount = ecCount[0] ? ecCount[0].certificateCount : 0;
      const rsaCertCount = rsaCount[0] ? rsaCount[0].certificateCount : 0;
      this.chartUtilService.plotBarChart(canvas.getContext('2d'),
        ['RSA', 'Elliptic Curve'], [ rsaCertCount,
          ecCertCount],
        rsaCertCount > ecCertCount ? rsaCertCount : ecCertCount);
    });
  }

}
