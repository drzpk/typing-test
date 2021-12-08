import {TestDefinitionModel} from "@/models/test-definition";

export interface TestStatsModel {
    testDefinition: TestDefinitionModel;
    finishedTests: number;
    userStats: StatsGroupModel;
    globalStats: StatsGroupModel;
    speedValues: Array<TimedDataModel<number>>;
    accuracyValues: Array<TimedDataModel<number>>;
}

export interface StatsGroupModel {
    averageSpeed: number;
    averageAccuracy: number;
}

export interface TimedDataModel<T> {
    timestamp: number;
    value: T;
}