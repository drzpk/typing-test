import {ActionContext, Module} from "vuex";
import {RootState} from "@/store/index";
import {TestDefinitionModel} from "@/models/test-definition";
import ApiService from "@/services/Api.service";
import {TestBestResultModel, TestModel, TestResultModel, TestStateModel} from "@/models/tests";
import {ErrorCode, ErrorCodeModel, ServerError} from "@/models/error";
import {WordListType} from "@/models/words";
import WordService from "@/services/Word.service";

export interface TestState {
    loading: boolean;
    activeTest: TestModel | undefined;
    activeTestStarted: boolean;
    testDefinitions: Array<TestDefinitionModel> | undefined;
    activeTestDefinition: TestDefinitionModel | undefined;
    testError: ErrorCodeModel | null;
    testResult: TestResultModel | null;
    testBestResults: TestBestResultModel[] | null;

    enteredWords: Array<string>;
    backspaceCount: number;

    testFinishDetectorCancelFn: (() => void) | null;
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
        testBestResults: null,

        enteredWords: [],
        backspaceCount: 0,

        testFinishDetectorCancelFn: null
    },

    getters: {
        isLoading(state) {
            return state.loading;
        },
        isTestResetAvailable(state) {
            return state.activeTest != null
                || state.activeTestDefinition != null
                && state.activeTestDefinition.wordList!.type == WordListType.RANDOM;
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
        },
        testBestResults(state): TestBestResultModel[] {
            if (state.testBestResults)
                return state.testBestResults;
            else
                return [];
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
        setTestBestResults(state, bestResults: TestBestResultModel[]) {
            state.testBestResults = bestResults;
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
            state.testResult = null;
        },
        setTestFinishDetectorCancelFn(state, value: (() => void) | null) {
            state.testFinishDetectorCancelFn = value;
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

            // If there was an active test before, cancel any pending handlers
            if (context.state.testFinishDetectorCancelFn) {
                context.state.testFinishDetectorCancelFn();
                context.commit("setTestFinishDetectorCancelFn", null);
            }

            const testDefinitionId = context.state.activeTestDefinition.id;
            context.commit("setLoading", true);
            ApiService.createTest(testDefinitionId).then(test => {
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
                return context.dispatch("reloadTestBestResults", testDefinitionId);
            });
        },

        resetTest(context: ActionContext<any, any>) {
            if (!context.getters.isTestResetAvailable)
                return;

            if (context.state.activeTest != null && context.state.activeTest.state == TestStateModel.CREATED) {
                context.commit("setLoading", true);

                ApiService.regenerateTestWordList(context.state.activeTest.id).then(test => {
                    context.commit("setActiveTest", test);
                }).catch((error: ServerError) => {
                    context.commit("setTestError", error.data);
                }).then(() => {
                    context.commit("setLoading", false);
                });
            } else if (context.state.activeTestDefinition != null) {
                // noinspection JSIgnoredPromiseFromCall
                context.dispatch("createTest");
            }
        },

        startTest(context: ActionContext<any, any>) {
            if (context.state.activeTest?.state != TestStateModel.CREATED)
                throw new Error("Cannot start test");

            // Start test silently
            context.commit("setActiveTestStarted", true);
            context.commit("resetTestStats");

            ApiService.startTest(context.state.activeTest.id).then(test => {
                context.commit("setActiveTest", test);

                if (test.definition.duration != null) {
                    const timeoutNo = setTimeout(() => {
                        if (context.state.activeTest?.id == test.id && context.state.activeTest?.state == test.state) {
                            // noinspection JSIgnoredPromiseFromCall
                            context.dispatch("finishTest");
                        }
                    }, test.definition.duration * 1000);

                    const stopHandle = () => clearTimeout(timeoutNo);
                    context.commit("setTestFinishDetectorCancelFn", stopHandle);

                } else {
                    // eslint-disable-next-line prefer-const
                    let stopHandle: (() => void);
                    const wordCount = WordService.countWords(context.state.activeTestDefinition.wordList);

                    const intervalNo = setInterval(() => {
                        if (context.state.activeTest?.id != test.id || context.state.activeTest?.state != test.state) {
                            stopHandle();
                            return;
                        }

                        console.log("entered words : ", context.state.enteredWords.length);
                        console.log("total words: " + wordCount);
                        if (context.state.enteredWords.length === wordCount) {
                            // noinspection JSIgnoredPromiseFromCall
                            context.dispatch("finishTest");
                        }
                    }, 500);

                    stopHandle = () => clearInterval(intervalNo);
                    context.commit("setTestFinishDetectorCancelFn", stopHandle);
                }

            }).catch((error: any) => {
                if (error instanceof ServerError)
                    context.commit("setTestError", error.data);
                else
                    console.error(error);
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
                return context.dispatch("getTestResult");
            }).then(() => {
                return context.dispatch("reloadTestBestResults", context.state.activeTestDefinition.id);
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
        },

        reloadTestBestResults(context: ActionContext<any, any>, testDefinitionId: number) {
            ApiService.getTestBestResults(testDefinitionId).then(bestResults => {
                context.commit("setTestBestResults", bestResults);
            })
        }
    }
};

export default testModule;