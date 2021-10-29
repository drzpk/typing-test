import {ErrorCodeModel} from "@/models/error";
import {Module} from "vuex";
import {RootState} from "@/store/index";

interface ConnectionState {
    pendingRequests: string[];
    error: {
        code: ErrorCodeModel | null;
        requestName: string | null;
    }
}

const connectionModule: Module<ConnectionState, RootState> = {
    state: {
      pendingRequests: [],
      error: {
          code: null,
          requestName: null
      }
    },
    getters: {
        pendingRequest(state): boolean {
            return state.pendingRequests.length > 0;
        },
        hasPendingRequest(state): (name: string) => boolean {
            return (name: string) => {
                return state.pendingRequests.indexOf(name) > -1;
            };
        },
        errorCode(state): ErrorCodeModel | null {
            return state.error ? state.error.code : null;
        },
        errorRequestName(state): string | null {
            return state.error ? state.error.requestName : null;
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
        setErrorCode(state, errorCode: ErrorCodeModel) {
            state.error.code = errorCode;
        },
        setErrorRequestName(state, requestName: string) {
            state.error.requestName = requestName;
        },
        clearError(state) {
            state.error.code = null;
            state.error.requestName = null;
        }
    }
};

export default connectionModule;