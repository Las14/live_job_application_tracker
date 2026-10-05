import { ChangeDetectorRef, Component, inject } from "@angular/core";
import { AuthService } from "../../../core/services/auth.service";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatInputModule } from "@angular/material/input";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { Router } from "@angular/router";
import { LoginRequest } from "../../../core/models/user.model";
import { MatIcon } from "@angular/material/icon";


@Component({
  selector: 'app-login',
  imports: [MatButtonModule, MatCardModule, MatInputModule, ReactiveFormsModule, MatFormFieldModule, MatIcon],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})

export class Login{
  private authservice = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  errorMessage = '';
  hidePassword = true;
  isLoading=false;

  loginForm = this.fb.group({
    email: ['',[ Validators.required, Validators.email]],
    password: ['',Validators.required]
  });

  onSubmit(): void{
        if(this.loginForm.invalid){
          this.loginForm.markAllAsTouched();
          return;
        }

        this.isLoading= true;
        this.errorMessage="";

        const loginData = this.loginForm.value as LoginRequest;
        this.authservice.login(loginData).subscribe({
          next:()=>{
            this.isLoading = false
            this.router.navigate(['/applicationList'])
          }, 
          error:(error) =>{
            this.isLoading= false;
            console.error('login failed', error);
            if(error.error && typeof error.error === 'object'){
              const firstMessage = Object.values(error.error)[0];
              this.errorMessage = (firstMessage as string) || 'Login failed.'
            }else{
              this.errorMessage= error.error || 'Login failed';
            }
            this.cdr.detectChanges();
          }
          });
  }

  togglePassword():void{
    this.hidePassword = !this.hidePassword;
    console.log('hisePassword:', this.hidePassword);
  }
}


