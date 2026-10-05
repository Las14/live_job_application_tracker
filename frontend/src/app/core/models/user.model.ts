export interface RegisterRequest {
    firstName: string;
    lastName: string;
    email: string;
    birthDate: string;
    password: string;
    passwordConfirmation: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface AuthResponse{
    token: string;
}

export interface UpdateUserRequest{
    firstName: string;
    lastName: string;
    birthDate: string;
}

export interface User{
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    birthDate: string;
}
