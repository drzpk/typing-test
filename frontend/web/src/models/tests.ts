import {TestDefinitionModel} from "@/models/test-definition";

export interface TestModel {
    id: number;
    state: TestStateModel;
    definition: TestDefinitionModel;
    createdAt: Date;
    startedAt: Date | undefined;
    finishedAt: Date | undefined;
    selectedWords: string;
    enteredWords: string;
    wordRegenerationCount: number;

    startDueTime: Date | undefined;
    finishDueTime: Date | undefined;
}

export interface TestResultModel {
    id: number;
    correctWords: number;
    incorrectWords: number;
    correctKeystrokes: number;
    incorrectKeystrokes: number;
    accuracy: number;
    wordsPerMinute: number;
    durationSeconds: number;
}

export interface TestBestResultModel {
    userDisplayName: string;
    testCreatedAt: Date;
    speed: number;
    accuracy: number;
    durationSeconds: number;
    score: number;
}

export enum TestStateModel {
    // noinspection JSUnusedGlobalSymbols
    CREATED = "CREATED",
    CREATED_TIMEOUT = "CREATED_TIMEOUT",
    STARTED = "STARTED",
    STARTED_TIMEOUT = "STARTED_TIMEOUT",
    FINISHED = "FINISHED"
}

export interface FinishTestRequest {
    enteredWords: string;
    backspaceCount: number;
}