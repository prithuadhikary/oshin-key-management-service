import {
  ActionReducer,
  ActionReducerMap,
  createFeatureSelector,
  createReducer,
  createSelector,
  MetaReducer,
  on
} from '@ngrx/store';
import { User } from 'src/app/shared/model/User';
import { UserProfile } from 'src/app/shared/model/UserProfile';
import { AuthActions } from './auth.actions';

export const authFeatureKey = 'auth';

export interface AuthState {
  user?: UserProfile
}

const initialState: AuthState = {
  user: undefined
} 

export const reducers = createReducer(
  initialState,
  on(AuthActions.login, (state, action) => {
    return state;
  }),
  on(AuthActions.loginSuccess, (state, action) => {
    return { ...state, user: action.profile };
  })  
)
