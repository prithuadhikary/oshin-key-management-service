import {Injectable} from '@angular/core';
import {Chart} from 'chart.js';

@Injectable({
  providedIn: 'root'
})
export class ChartUtilService {

  public plotBarChart(context: any, labels: Array<string>, chartData: Array<number>, maxValue: number): void {
    // tslint:disable-next-line:no-unused-expression
    new Chart(context, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          data: chartData,
          backgroundColor: [
            '#6e8bed',
            'rgb(140,99,208)',
            'rgb(255, 206, 86)',
            'rgb(75, 192, 192)',
            'rgb(182,7,236)',
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
        plugins: {
          legend: {
            display: false
          }
        },
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
