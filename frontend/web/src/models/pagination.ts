export interface PageMetadata {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export class PagedRequest {
    page: number;
    size: number;

    constructor(page = 1, size = 10) {
        this.page = page;
        this.size = size;
    }

    static fromPageMetadata(metadata: PageMetadata): PagedRequest {
        return new PagedRequest(metadata.page, metadata.size);
    }
}