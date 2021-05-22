import {ActionContext, Module} from "vuex";
import {RootState} from "@/store/index";
import {TestDefinitionModel} from "@/models/test-definition";
import ApiService from "@/services/Api.service";

export interface TestState {
    inProgress: boolean;
    activeTest: Test | undefined;

    testDefinitions: Array<TestDefinitionModel> | undefined;
    activeTestDefinition: TestDefinitionModel | undefined
}

export interface Test {
    id: number;
}

export interface ActiveTest extends Test {
    startTime: Date;
    endTime: Date | undefined;

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
        }
    },

    mutations: {
        setUserTestDefinitions(state, definitions: Array<TestDefinitionModel>) {
            state.testDefinitions = definitions;
        },
        setActiveUserTestDefinition(state, definition: TestDefinitionModel) {
            state.activeTestDefinition = definition;
        }
    },

    actions: {
        refreshUserTestDefinitions(context: ActionContext<any, any>) {
            ApiService.getTestDefinitions().then((definitions) => {
                context.commit("setUserTestDefinitions", definitions);
                context.commit("setActiveUserTestDefinition", definitions.length > 0 ? definitions[0] : undefined);
            })
        }
    }
};

export default testModule;