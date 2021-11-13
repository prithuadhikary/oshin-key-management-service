import {Component, Input, OnInit} from '@angular/core';
import {Certificate} from '../../model/Certificate';
import {KeyUsage} from '../../model/KeyUsage';

@Component({
  selector: 'app-certificate-info',
  templateUrl: './certificate-info.component.html',
  styleUrls: ['./certificate-info.component.scss']
})
export class CertificateInfoComponent {

  private readonly maxPathLength = 2147483647;

  @Input() certificate: Certificate;

  constructor() { }

  getKeyUsages(): Array<string> {
    return this.certificate.keyUsages.map(value => {
      return KeyUsage[value];
    });
  }

  get pathLengthConstraint(): any {
    if (this.certificate.pathLengthConstraint === this.maxPathLength) {
      return 'Unlimited';
    } else {
      return this.certificate.pathLengthConstraint;
    }
  }

}
