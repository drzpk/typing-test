import {ActionContext} from "vuex";
import {ServerError} from "@/models/error";

export function withPendingRequest<T>(requestName: string, context: ActionContext<any, any>, callback: () => Promise<T>): Promise<T> {
    return new Promise(resolve => {
        context.commit("addPendingRequest", requestName);
        resolve(null);
    }).then(() => {
        return callback();
    }).then((result: T) => {
        context.commit("clearPendingRequest", requestName);
        return result;
    }).catch(e => {
        context.commit("clearPendingRequest", requestName);

        if (e instanceof ServerError) {
            if (context.state.error && context.state.error.code)
                context.commit("clearError");

            const model = e.data;
            context.commit("setErrorCode", model);
            context.commit("setErrorRequestName", requestName);
        }

        throw e;
    });
}