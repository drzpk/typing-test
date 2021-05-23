import {ActionContext, Module} from "vuex";
import {RootState} from "@/store/index";
import {TestDefinitionModel} from "@/models/test-definition";
import ApiService from "@/services/Api.service";
import {TestModel, TestResultModel, TestStateModel} from "@/models/tests";
import {ErrorCode, ErrorCodeModel, ServerError} from "@/models/error";

export interface TestState {
    loading: boolean;
    activeTest: TestModel | undefined;
    activeTestStarted: boolean;
    testDefinitions: Array<TestDefinitionModel> | undefined;
    activeTestDefinition: TestDefinitionModel | undefined;
    testError: ErrorCodeModel | null;
    testResult: TestResultModel | null;

    enteredWords: Array<string>;
    backspaceCount: number;
}

const testModule: Module<TestState, RootState> = {
    state: {
        loading: false,
        activeTest: undefined,
        activeTestStarted: false,
        testDefinitions: undefined,
        activeTestDefinition: undefined,
        testError: null,
        testResult: null,

        enteredWords: [],
        backspaceCount: 0
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
        activeTestStarted(state) {
            return state.activeTestStarted;
        },
        testState(state): TestStateModel | undefined {
            if (state.activeTest)
                return state.activeTest.state;
            else
                return undefined;
        },
        testError(state): ErrorCodeModel | null {
            return state.testError;
        },
        testResult(state): TestResultModel | null {
            return state.testResult;
        }
    },

    mutations: {
        setLoading(state, loadingState) {
            state.loading = loadingState;
        },
        setUserTestDefinitions(state, definitions: Array<TestDefinitionModel>) {
            state.testDefinitions = definitions;
            state.activeTestStarted = false;
            state.testError = null;
        },
        setActiveUserTestDefinition(state, definition: TestDefinitionModel) {
            state.activeTestDefinition = definition;
            state.activeTestStarted = false;
            state.testResult = null;
            state.testError = null;
        },
        setActiveTest(state, test: TestModel | undefined) {
            state.activeTest = test;
            state.activeTestStarted = test?.state === TestStateModel.STARTED ? state.activeTestStarted : false;
            state.testError = null;
        },
        setActiveTestStarted(state, started: boolean) {
            state.activeTestStarted = started;
        },
        setTestError(state, error: ErrorCodeModel) {
            state.loading = false;
            state.testError = error;
            console.error("Test error", error);
        },
        setTestResult(state, result: TestResultModel) {
            state.testResult = result;
        },
        addEnteredWord(state, newWord) {
            state.enteredWords.push(newWord);
        },
        incrementBackspaceCount(state) {
            state.backspaceCount++;
        },
        resetTestStats(state) {
            state.enteredWords = [];
            state.backspaceCount = 0;
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
            if (context.state.activeTest?.state != TestStateModel.CREATED)
                throw new Error("Cannot start test");

            // Start test silently
            context.commit("setActiveTestStarted", true);
            context.commit("resetTestStats");

            ApiService.startTest(context.state.activeTest.id).then(test => {
                context.commit("setActiveTest", test);

                setTimeout(() => {
                    if (context.state.activeTest?.id == test.id && context.state.activeTest?.state == test.state) {
                        // noinspection JSIgnoredPromiseFromCall
                        context.dispatch("finishTest");
                    }
                }, test.definition.duration * 1000);
            }).catch((error: ServerError) => {
               context.commit("setTestError", error.data);
            });
        },

        finishTest(context: ActionContext<any, any>) {
            if (context.state.activeTest?.state != TestStateModel.STARTED)
                throw new Error("Cannot finish test");

            context.commit("setActiveTestStarted", false);

            const payload = {
                enteredWords: context.state.enteredWords.join("|"),
                backspaceCount: context.state.backspaceCount
            };
            ApiService.finishTest(context.state.activeTest.id, payload).then(test => {
                context.commit("setActiveTest", test);
                // noinspection JSIgnoredPromiseFromCall
                context.dispatch("getTestResult");
            }).catch((error: ServerError) => {
                context.commit("setTestError", error);
            })
        },

        getTestResult(context: ActionContext<any, any>) {
            context.commit("setLoading", true);
            ApiService.getTestResult(context.state.activeTest!.id).then(result => {
                context.commit("setTestResult", result);
            }).catch((error: ServerError) => {
                context.commit("setTestError", error);
            }).then(() => {
                context.commit("setLoading", false);
            });
        }
    }
};

export default testModule;