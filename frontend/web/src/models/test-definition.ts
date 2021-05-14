import {WordList} from "@/models/words";

export interface CreateUpdateTestDefinitionRequest {
    name: string | null;
    duration: number | null;
    wordListId: number | null;
    isActive: boolean | null;
}

export interface TestDefinitionModel {
    id: number;
    name: string;
    wordList: WordList | null;
    duration: number;
    isActive: boolean;
    createdAt: Date | null;
    modifiedAt: Date | null;
}
