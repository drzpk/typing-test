import {ActionContext} from "vuex";

export function withPendingRequest<T>(requestName: string, context: ActionContext<any, any>, callback: () => Promise<T>): Promise<T> {
    try {
        return new Promise(resolve => {
            context.commit("addPendingRequest", requestName);
            resolve(null);
        }).then(() => {
            return callback();
        }).then((result: T) => {
            context.commit("clearPendingRequest", requestName);
            return result;
        });
    } catch (e) {
        context.commit("clearPendingRequest", requestName);
        throw e;
    }
}