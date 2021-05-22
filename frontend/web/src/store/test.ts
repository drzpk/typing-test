import {ActionContext, Module} from "vuex";
import {RootState} from "@/store/index";
import {TestDefinitionModel} from "@/models/test-definition";
import ApiService from "@/services/Api.service";
import {TestModel, TestStateModel} from "@/models/tests";

export interface TestState {
    inProgress: boolean;
    activeTest: TestModel | undefined;

    testDefinitions: Array<TestDefinitionModel> | undefined;
    activeTestDefinition: TestDefinitionModel | undefined
}

const testModule: Module<TestState, RootState> = {
    state: {
        inProgress: false,
        activeTest: undefined,
        testDefinitions: undefined,
        activeTestDefinition: undefined
    },

    getters: {
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
    },

    mutations: {
        setUserTestDefinitions(state, definitions: Array<TestDefinitionModel>) {
            state.testDefinitions = definitions;
        },
        setActiveUserTestDefinition(state, definition: TestDefinitionModel) {
            state.activeTestDefinition = definition;
        },
        setActiveTest(state, test: TestModel) {
            state.activeTest = test;
        }
    },

    actions: {
        refreshUserTestDefinitions(context: ActionContext<any, any>) {
            ApiService.getTestDefinitions().then((definitions) => {
                context.commit("setUserTestDefinitions", definitions);
                //context.commit("setActiveUserTestDefinition", definitions.length > 0 ? definitions[0] : undefined);
            })
        },

        createTest(context: ActionContext<any, any>) {
            if (!context.state.activeTestDefinition)
                throw new Error("Cannot start test, there's no active test definition");

            ApiService.createTest(context.state.activeTestDefinition.id).then(test => {
                context.commit("setActiveTest", test);
            })
        },

        startTest(context: ActionContext<any, any>) {
            if (context.state.activeTestDefinition?.state != TestStateModel.CREATED)
                throw new Error("Cannot start test");


        }
    }
};

export default testModule;