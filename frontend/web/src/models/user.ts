export interface AuthenticationDetails {
    userId: number;
    email: string;
    displayName: string;
    isAdmin: boolean;
}

export interface UpdateSettingsRequest {
    displayName: string;
}

export interface ChangePasswordRequest {
    oldPassword: string;
    newPassword: string;
}
