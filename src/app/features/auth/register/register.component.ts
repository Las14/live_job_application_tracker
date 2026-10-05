import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../core/models/user.model';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink,MatFormFieldModule, MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})

export class Register {
  private fb = inject(FormBuilder);
  private authservice = inject(AuthService);
  private router = inject(Router);
  errorMessage = '';
  hidePassword= true;
  hideConfirmPassword = true
 
 private passwordsMatchValidator: ValidatorFn = 
    (control:AbstractControl): ValidationErrors | null =>{ //control represent the whole registerForm
      const password = control.get('password')?.value;
      const passwordConfirmation = control.get('passwordConfirmation')?.value;
      if(password === passwordConfirmation){
        return null
      }
      return {passwordsMisMatch: true};
    };

    private minimumAgeValidator(minAge: number): ValidatorFn{
      return(control: AbstractControl): ValidationErrors | null =>{
        if(!control.value){
          return null;
        }
        const birthDate = new Date(control.value);
        const today = new Date();

      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDifference = today.getMonth() - birthDate.getMonth();

      if(monthDifference <0 ||(monthDifference === 0 && today.getDate()< birthDate.getDate()) ){
        age--;
      }
        return age >= minAge ? null : {minimumAge : true}
      };
    }
 
  registerForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['',[ Validators.required, Validators.email]],
    birthDate: ['',[ Validators.required, this.minimumAgeValidator(16)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    passwordConfirmation: ['', Validators.required]},
    { validators: this.passwordsMatchValidator});

    onSubmit(): void{
      if(this.registerForm.invalid){
        this.registerForm.markAllAsTouched();
        return;
      }

      const data = this.registerForm.value as RegisterRequest;
      this.authservice.register(data).subscribe({
        next:(response) =>{
          console.log('Registration successful:', response);
          this.router.navigate(['/login'])
        }, 
        error: (error) =>{
          console.error('Registration failed:', error);
          if(error.error && typeof error.error === 'object'){
            const firstMessage = Object.values(error.error)[0];
            this.errorMessage = (firstMessage as string) || 'Registration faild.'
          }else{
            this.errorMessage= error.error || 'Registration faild.';
          }
        }
      });
    }

     togglePassword():void{
    this.hidePassword = !this.hidePassword;
    console.log('hisePassword:', this.hidePassword);
  }
  togglePasswordConfirmation():void{
    this.hideConfirmPassword = !this.hideConfirmPassword;
    console.log('hideConfirmPassword:', this.hideConfirmPassword);
  }
    
}
