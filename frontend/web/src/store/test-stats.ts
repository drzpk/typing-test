import {ActionContext, Module} from "vuex";
import {RootState} from "@/store/index";
import ApiService from "@/services/Api.service";
import {TestDefinitionModel} from "@/models/test-definition";
import {TestStatsModel} from "@/models/test-stats";

interface TestStatsState {
    testDefinitionsWithStats: Array<TestDefinitionModel> | null;
    testStats: TestStatsModel | null;
}

const testStatsModule: Module<TestStatsState, RootState> = {
    state: {
        testDefinitionsWithStats: null,
        testStats: null
    },

    getters: {
        testDefinitionsWithStats(state): Array<TestDefinitionModel> {
            if (state.testDefinitionsWithStats != null)
                return state.testDefinitionsWithStats;
            else
                return [];
        },
        testStats(state): TestStatsModel | null {
            return state.testStats;
        }
    },

    mutations: {
        setTestDefinitionsWithStats(state, value: Array<TestDefinitionModel> | null) {
            state.testDefinitionsWithStats = value;
        },
        setTestStats(state, value: TestStatsModel | null) {
            state.testStats = value;
        }
    },

    actions: {
        refreshTestDefinitionWithStats(context: ActionContext<any, any>) {
            ApiService.getTestDefinitionsWithStats().then(response => {
                context.commit("setTestDefinitionsWithStats", response);
            });
        },

        refreshTestStats(context: ActionContext<any, any>, testDefinitionId: number) {
            ApiService.getStatsForTestDefinition(testDefinitionId).then(response => {
                context.commit("setTestStats", response);
            });
        }
    }
};

export default testStatsModule;