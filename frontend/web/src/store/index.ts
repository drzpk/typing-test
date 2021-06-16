import ApiService from '@/services/Api.service'
import Vue from 'vue'
import Vuex, {ActionContext} from 'vuex'
import {AxiosError} from "axios";
import {AuthenticationDetails} from "@/models/user";
import admin from "./admin";
import test from "./test";
import testStats from "./test-stats";

Vue.use(Vuex);

export interface RegisterData {
    email: string;
    displayName: string;
    password: string;
}

export interface LoginData {
    email: string;
    password: string;
}

export interface RootState {
    applicationLoaded: boolean;
    authenticationDetails: AuthenticationDetails | null;
}

export default new Vuex.Store<RootState>({
    state: {
        applicationLoaded: false,
        authenticationDetails: null
    },
    getters: {
        isLoggedIn(state): boolean {
            return state.authenticationDetails != null;
        }
    },
    mutations: {
        markApplicationLoaded(state) {
            state.applicationLoaded = true;
        },

        setAuthenticationDetails(state, details) {
            state.authenticationDetails = details;
            state.applicationLoaded = true;
        }
    },
    actions: {
        loadApplication(context: ActionContext<any, any>) {
            ApiService.getAuthenticationDetails().then((details) => {
                context.commit("setAuthenticationDetails", details);
            }).catch((error: AxiosError) => {
                context.commit("markApplicationLoaded");
                throw error;
            });
        },

        register(context: ActionContext<any, any>, registerData: RegisterData) {
            return ApiService.register(registerData.email, registerData.displayName, registerData.password);
        },

        login(context: ActionContext<any, any>, loginData: LoginData) {
            return ApiService.login(loginData.email, loginData.password).then(authenticationDetails => {
                context.commit("setAuthenticationDetails", authenticationDetails);
            });
        },

        logout(context: ActionContext<any, any>) {
            return ApiService.logout().then(() => {
               context.commit("setAuthenticationDetails", null);
            });
        },

        changeDisplayName(context: ActionContext<any, any>, newDisplayName: string) {
            const request = {
                displayName: newDisplayName
            };

            return new Promise(resolve => {
                ApiService.updateSettings(request)
                    .then(() => {
                    if (context.state.authenticationDetails) {
                        context.state.authenticationDetails.displayName = newDisplayName;
                    }

                    resolve(null);
                });
            });
        }
    },
    modules: {
        admin,
        test,
        testStats
    }
})
