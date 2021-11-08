import { Component, OnInit } from '@angular/core';
import {Chart, registerables} from 'chart.js';

@Component({
  selector: 'app-certificate-expiry-density',
  templateUrl: './certificate-expiry-density.component.html',
  styleUrls: ['./certificate-expiry-density.component.scss']
})
export class CertificateExpiryDensityComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
      Chart.register(...registerables);
      const canvas: any = document.getElementById('canvas');
      const context = canvas.getContext('2d');
      const mychart = new Chart(context, {
        type: 'bar',
        data: {
          labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July',
            'August', 'September', 'October', 'November', 'December'],
          datasets: [{
            label: 'DS',
            data: [80, 90, 100, 30, 40, 50, 45, 12, 34, 56, 67, 89],
            backgroundColor: [
              'rgba(255, 99, 132, 0.2)',
              'rgba(54, 162, 235, 0.2)',
              'rgba(255, 206, 86, 0.2)',
              'rgba(75, 192, 192, 0.2)',
              'rgba(153, 102, 255, 0.2)',
              'rgba(255, 159, 64, 0.2)'
            ]
          }]
        }
      });
  }


}
