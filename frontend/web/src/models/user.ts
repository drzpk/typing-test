import {PageMetadata} from "@/models/pagination";

export interface AuthenticationDetails {
    userId: number;
    email: string;
    displayName: string;
    isAdmin: boolean;
    isAnonymous: boolean;
}

export interface UpdateSettingsRequest {
    displayName: string;
}

export interface ChangePasswordRequest {
    oldPassword: string;
    newPassword: string;
}

export interface UserModel {
    id: number;
    email: string;
    displayName: string;
    createdAt: number;
    activatedAt: number | null;
}

export interface SearchUsersRequest {
    page: number | null;
    size: number | null;
    inactiveOnly: boolean;
}

export interface SearchUsersResponse {
    content: UserModel[];
    metadata: PageMetadata;
}
