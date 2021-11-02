import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl} from '@angular/forms';
import {debounceTime} from 'rxjs/operators';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  searchInputControl = new FormControl('');

  @Output() searchTextChange: EventEmitter<string> = new EventEmitter<string>();

  constructor() { }

  private readonly _DEBOUNCE_DUE_TIME = 700;

  ngOnInit(): void {
    this.searchInputControl.valueChanges
      .pipe(
        debounceTime(this._DEBOUNCE_DUE_TIME)
      )
      .subscribe(value => this.searchTextChange.emit(value));
  }

  notEmpty(): boolean {
    return this.searchInputControl.value.length > 0;
  }

}
