import {Component, OnInit} from '@angular/core';
import {Chart, registerables} from 'chart.js';
import {CertificateService} from '../../../../shared/services/certificate.service';
import * as moment from 'moment';
import {ChartUtilService} from '../../../../shared/services/chart-util.service';

@Component({
  selector: 'app-certificate-issue-density',
  templateUrl: './certificate-issue-density.component.html',
  styleUrls: ['./certificate-issue-density.component.scss']
})
export class CertificateIssueDensityComponent implements OnInit {

  constructor(
    private certificateService: CertificateService,
    private chartUtilService: ChartUtilService
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
        this.chartUtilService.plotBarChart(context, labels, chartData, maxValue);
      });
  }



}
