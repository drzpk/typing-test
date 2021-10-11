import {ActionContext} from "vuex";

export function withPendingRequest<T>(requestName: string, context: ActionContext<any, any>, callback: () => Promise<T>): Promise<T> {
    context.commit("addPendingRequest", requestName);
    try {
        return callback()
            .then((result: T) => {
                context.commit("clearPendingRequest", requestName);
                return result;
            });
    } catch (e) {
        context.commit("clearPendingRequest", requestName);
        throw e;
    }
}