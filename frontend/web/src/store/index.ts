import ApiService from '@/services/Api.service'
import Vue from 'vue'
import Vuex, {ActionContext} from 'vuex'
import {AxiosError} from "axios";
import router from "@/router";
import {AuthenticationDetails} from "@/models/user";
import admin from "./admin";
import test from "./test";
import testStats from "./test-stats";

Vue.use(Vuex);

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
                return router.push({name: "TestPage"});
            }).catch((error: AxiosError) => {
                if (error.response?.status != 401) {
                    console.error("Unexpected error while getting authentication details: " + error.response?.data)
                }
                router.push({name: "Login"})
                    .catch((error) => {
                        if (error.name !== "NavigationDuplicated")
                            throw error;
                    })
                    .then(() => context.commit("markApplicationLoaded"))
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
