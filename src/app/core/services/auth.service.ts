import { Injectable, signal } from '@angular/core';
import{
    RegisterRequest, 
    LoginRequest, 
    AuthResponse, 
    User, 
    UpdateUserRequest
} from '../models/user.model'
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AuthService{
    private apiUrl = 'http://localhost:8080/api/users';
    private tokenKey = 'token';
    

    constructor(private http: HttpClient){}

    register(data: RegisterRequest): Observable<string>{
        return this.http.post(
            `${this.apiUrl}/register`, 
            data, 
            {responseType: 'text'}
        );
    }

    login(data: LoginRequest): Observable<AuthResponse>{
        return this.http.post<AuthResponse>(
            `${this.apiUrl}/login`, 
            data
        ).pipe(
            tap(response => {
                localStorage.setItem(this.tokenKey, response.token)
            })
        );
    }

    getToken(): string|null{
        return localStorage.getItem(this.tokenKey);
    }

    logout():void{
         localStorage.removeItem(this.tokenKey);
         this.currentUser.set(null);

    }

    isLoggedIn(): boolean{
        return !!localStorage.getItem(this.tokenKey);
    }

    getCurrentUser(): Observable<User>{
        return this.http.get<User>(
            `${this.apiUrl}/profile`
        );
    }

    updateCurrentUser(data: UpdateUserRequest): Observable<User>{
        return this.http.put<User>(
            `${this.apiUrl}/profile`,
            data
        );
    }
    currentUser = signal<User | null>(null);

    loadCurrentUser():void{
        if(!this.isLoggedIn() || this.currentUser()) return;
        this.getCurrentUser().subscribe({
            next:(user: User) => this.currentUser.set(user),
            error:() => {}
        })
    }

    

}