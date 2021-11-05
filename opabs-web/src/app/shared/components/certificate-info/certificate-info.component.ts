import {Component, Input, OnInit} from '@angular/core';
import {Certificate} from '../../model/Certificate';
import {KeyUsage} from '../../model/KeyUsage';
import { faInfoCircle } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-certificate-info',
  templateUrl: './certificate-info.component.html',
  styleUrls: ['./certificate-info.component.scss']
})
export class CertificateInfoComponent {

  faInfoCircle = faInfoCircle;

  @Input() certificate: Certificate;

  constructor() { }

  getKeyUsages(): Array<string> {
    return this.certificate.keyUsages.map(value => {
      return KeyUsage[value];
    });
  }

}
