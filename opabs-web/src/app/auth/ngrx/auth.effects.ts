import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { AuthActions } from "./auth.actions";
import { tap } from 'rxjs/operators';
import { Store } from "@ngrx/store";
import { AuthState } from "./auth.reducers";
import { Router } from "@angular/router";

@Injectable()
export class AuthEffects {

    constructor(
        private _actions: Actions,
        private _store: Store<AuthState>,
        private _router: Router
    ) { }

    loginActions$ = createEffect(() => this._actions.pipe(
        ofType(AuthActions.login),
        tap(action => {
            this._store.dispatch(AuthActions.loginSuccess({
                profile: {
                    id: '',
                    firstName: 'Prithu',
                    lastName: 'Adhikary',
                    addresses: [{
                        addressLine1: 'F701, Surobhi Township',
                        addressLine2: 'Tingare Nagar',
                        city: 'Pune',
                        stateOrProvince: 'Maharashtra',
                        country: 'India',
                        zipCode: '411015'
                    }]
                }
            }));
            this._router.navigate(['protected', 'dashboard']);
        }
        )), { dispatch: false });

}
