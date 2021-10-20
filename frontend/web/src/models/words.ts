import {PageMetadata} from "@/models/pagination";

export interface WordListModel {
    id: number;
    name: string;
    language: string;
    type: WordListType;
}

export interface WordListWordsResponse {
    content: Array<WordListWord>;
    metadata: PageMetadata;
}

export interface WordListWord {
    id: number;
    word: string;
    popularity: number;
}

export enum WordListType {
    RANDOM = "RANDOM",
    FIXED = "FIXED",

}
