import {ErrorCodeModel} from "@/models/error";
import {Module} from "vuex";
import {RootState} from "@/store/index";

interface ConnectionState {
    pendingRequests: string[];
    errorCode: ErrorCodeModel | null;
}

const connectionModule: Module<ConnectionState, RootState> = {
    state: {
      pendingRequests: [],
      errorCode: null
    },
    getters: {
        pendingRequest(state): boolean {
            return state.pendingRequests.length > 0;
        },
        hasPendingRequest(state): (name: string) => boolean {
            return (name: string) => {
                return state.pendingRequests.indexOf(name) > -1;
            };
        }
    },
    mutations: {
        addPendingRequest(state, name: string) {
            if (state.pendingRequests.indexOf(name) === -1)
            state.pendingRequests.push(name);
        },
        clearPendingRequest(state, name: string) {
            const index = state.pendingRequests.indexOf(name);
            if (index > -1)
                state.pendingRequests.splice(index, 1);
        },
        setErrorCode(state, errorCode: ErrorCodeModel | null) {
            state.errorCode = errorCode;
        }
    }
};

export default connectionModule;