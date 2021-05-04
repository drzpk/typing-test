import {PageMetadata} from "@/models/pagination";

export interface WordList {
    id: number;
    name: string;
    language: string;
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
