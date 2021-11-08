import { Component, OnInit } from '@angular/core';
import * as c3 from 'c3';

@Component({
  selector: 'app-certificate-expiry-density',
  templateUrl: './certificate-expiry-density.component.html',
  styleUrls: ['./certificate-expiry-density.component.scss']
})
export class CertificateExpiryDensityComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
    const chart = c3.generate({
      bindto: '#expiry-density-chart',
      data: {
        columns: [
          ['data1', 30, 200, 100, 400, 150, 250],
        ],
        type: 'spline'
      }
    });
  }


}
