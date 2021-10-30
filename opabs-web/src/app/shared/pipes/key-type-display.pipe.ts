import { Pipe, PipeTransform } from '@angular/core';
import {KeyType} from '../model/KeyType';

@Pipe({
  name: 'keyTypeDisplay'
})
export class KeyTypeDisplayPipe implements PipeTransform {

  transform(value: string): string {
    switch (value) {
      case 'ELLIPTIC_CURVE':
        return 'Elliptic Curve';
      case 'RSA':
        return 'RSA';
    }
    return null;
  }

}
