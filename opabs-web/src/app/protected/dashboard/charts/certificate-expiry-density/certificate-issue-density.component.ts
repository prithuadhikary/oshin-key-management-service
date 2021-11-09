import {Component, OnInit} from '@angular/core';
import {Chart, registerables} from 'chart.js';
import {CertificateService} from '../../../../shared/services/certificate.service';
import * as moment from 'moment';

@Component({
  selector: 'app-certificate-issue-density',
  templateUrl: './certificate-issue-density.component.html',
  styleUrls: ['./certificate-issue-density.component.scss']
})
export class CertificateIssueDensityComponent implements OnInit {

  constructor(
    private certificateService: CertificateService
  ) { }

  ngOnInit(): void {
      Chart.register(...registerables);
      const now = Date();
      const startDate = moment(now).subtract(6, 'month').startOf('month');
      const endDate = moment(now).add(6, 'month').endOf('month');
      this.certificateService.fetchIssuedCountByDate(startDate.toISOString(), endDate.toISOString()).subscribe(data => {
        const canvas: any = document.getElementById('canvas');
        const context = canvas.getContext('2d');

        const labels = [];
        const chartData: Array<number> = [];
        let maxValue = -1;
        for (let i = 0; i < 12; i++) {
          const month = startDate.startOf('month').add(1, 'month').format('MMMM-YY');
          labels.push(month);
          const filteredItems = data.filter(item => item.month === month);
          if (filteredItems.length > 0) {
            chartData.push(filteredItems[0].count);
            if (filteredItems[0].count > maxValue) {
              maxValue = filteredItems[0].count;
            }
          } else {
            chartData.push(0);
          }
        }
        this.plotChart(context, labels, chartData, maxValue);
      });
  }

  plotChart(context: any, labels: Array<string>, chartData: Array<number>, maxValue: number): void {
    const myChart = new Chart(context, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Certificate Issued - Summary',
          data: chartData,
          backgroundColor: [
            'rgb(255, 99, 132)',
            'rgb(54, 162, 235)',
            'rgb(255, 206, 86)',
            'rgb(75, 192, 192)',
            'rgb(153, 102, 255)',
            'rgb(255, 159, 64)',
            'rgb(133,204,88)',
            'rgb(3,98,185)',
            'rgb(232,99,57)',
            'rgb(2,192,176)',
            'rgb(141,41,210)',
            'rgb(9,190,148)'
          ]
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            title: {
              display: true,
              text: 'No. Of Certificates'
            },
            suggestedMax: Math.ceil(maxValue * 1.5),
            ticks: {
              stepSize: 1
            }
          }
        }
      }
    });
  }

}
