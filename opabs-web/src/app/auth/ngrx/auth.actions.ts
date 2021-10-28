import { createAction, props } from "@ngrx/store";
import { LoginRequest } from "src/app/shared/model/LoginRequest";
import { UserProfile } from "src/app/shared/model/UserProfile";

export const login = createAction(
    '[Login Form] Login',
    props<LoginRequest>()
);

export const loginSuccess = createAction(
    '[Login Form] Login Successful',
    props<{ profile: UserProfile}>()
);

export const loginFailure = createAction(
    '[Login Form ] Login Failure',
    props<{ reason: string }>()
)

export const logout = createAction(
    '[Nav Bar] Logout'
);

export const AuthActions = {
    login,
    loginSuccess,
    loginFailure,
    logout
}