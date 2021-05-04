export interface PageMetadata {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export class PagedRequest {
    page = 1;
    size = 10;
}