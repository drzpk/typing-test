import {TestDefinitionModel} from "@/models/test-definition";

export interface TestStatsModel {
    testDefinition: TestDefinitionModel;
    finishedTests: number;
    averageWordsPerMinute: number;
    averageAccuracy: number;
    wordsPerMinuteValues: Array<TimedDataModel<number>>;
    accuracyValues: Array<TimedDataModel<number>>;
}

export interface TimedDataModel<T> {
    timestamp: number;
    value: T;
}