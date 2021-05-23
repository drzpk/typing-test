import {ActionContext, Module} from "vuex";
import {RootState} from "@/store/index";
import {TestDefinitionModel} from "@/models/test-definition";
import ApiService from "@/services/Api.service";
import {TestModel, TestStateModel} from "@/models/tests";
import {ErrorCode, ErrorCodeModel, ServerError} from "@/models/error";

export interface TestState {
    loading: boolean;
    activeTest: TestModel | undefined;
    testDefinitions: Array<TestDefinitionModel> | undefined;
    activeTestDefinition: TestDefinitionModel | undefined;
    testError: ErrorCodeModel | null;
}

const testModule: Module<TestState, RootState> = {
    state: {
        loading: false,
        activeTest: undefined,
        testDefinitions: undefined,
        activeTestDefinition: undefined,
        testError: null
    },

    getters: {
        isLoading(state) {
            return state.loading;
        },
        userTestDefinitions(state) {
            if (state.testDefinitions)
                return state.testDefinitions;
            else
                return [];
        },
        activeUserTestDefinition(state) {
            return state.activeTestDefinition;
        },
        activeTest(state) {
            return state.activeTest;
        },
        testState(state): TestStateModel | undefined {
            if (state.activeTest)
                return state.activeTest.state;
            else
                return undefined;
        },
        testError(state): ErrorCodeModel | null {
            return state.testError;
        }
    },

    mutations: {
        setLoading(state, loadingState) {
            state.loading = loadingState;
        },
        setUserTestDefinitions(state, definitions: Array<TestDefinitionModel>) {
            state.testDefinitions = definitions;
            state.testError = null;
        },
        setActiveUserTestDefinition(state, definition: TestDefinitionModel) {
            state.activeTestDefinition = definition;
            state.testError = null;
        },
        setActiveTest(state, test: TestModel) {
            state.activeTest = test;
            state.testError = null;
        },
        setTestError(state, error: ErrorCodeModel) {
            state.loading = false;
            state.testError = error;
            console.error("Test error", error);
        }
    },

    actions: {
        refreshUserTestDefinitions(context: ActionContext<any, any>) {
            context.commit("setLoading", true);
            ApiService.getTestDefinitions().then((definitions) => {
                context.commit("setUserTestDefinitions", definitions);
                //context.commit("setActiveUserTestDefinition", definitions.length > 0 ? definitions[0] : undefined);
                context.commit("setLoading", false);
            });
        },

        createTest(context: ActionContext<any, any>) {
            if (!context.state.activeTestDefinition)
                throw new Error("Cannot start test, there's no active test definition");

            context.commit("setLoading", true);
            ApiService.createTest(context.state.activeTestDefinition.id).then(test => {
                context.commit("setActiveTest", test);

                if (test.startDueTime != null) {
                    const diffMillis = test.startDueTime.getTime() - new Date().getTime();
                    setTimeout(() => {
                        if (context.state.activeTest === test && test.state === TestStateModel.CREATED) {
                            // noinspection JSIgnoredPromiseFromCall
                            context.dispatch("createTest");
                        }
                    }, diffMillis);
                }
            }).catch((error: ServerError) => {
                if (error.data.code === ErrorCode.UNKNOWN_ERROR) {
                    // Make this message a little more specific
                    error.data.message = "Unknown error while initiating a test. Click the restart button or select another test.";
                }

                context.commit("setActiveUserTestDefinition", null);
                context.commit("setActiveTest", null);
                context.commit("setTestError", error.data);
            }).then(() => {
                context.commit("setLoading", false);
            });
        },

        startTest(context: ActionContext<any, any>) {
            if (context.state.activeTestDefinition?.state != TestStateModel.CREATED)
                throw new Error("Cannot start test");

        }
    }
};

export default testModule;