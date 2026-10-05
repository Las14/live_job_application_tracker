import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
@Component({
  selector: 'app-profile',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements  OnInit{

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  isLoading = signal(true);
  isSaving = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  profileForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: [{value:'', disabled:true}],
    birthDate: ['', Validators.required]
  });


    ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void{
    this.isLoading.set(true);
    this.successMessage.set('');

    this.authService.getCurrentUser().subscribe({
      next: (user)  =>{
        this.profileForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          birthDate: user.birthDate

        });
        this.isLoading.set(false);
      }, 
      error: (error) =>{
        console.error('Failed to load profile', error);
        this.errorMessage.set('Could not load your profile.');
        this.isLoading.set(false);
      }
    });
  }
  onSubmit(): void{
    if(this.profileForm.invalid){
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const formValue = this.profileForm.getRawValue();
    const rowBirthDate = formValue.birthDate as any; 
    const data = {
      firstName : formValue.firstName ?? '',
      lastName : formValue.lastName ?? '',
      birthDate : rowBirthDate instanceof Date 
      ? rowBirthDate.toISOString().split('T')[0]
      :rowBirthDate ?? ''
    };

    this.authService.updateCurrentUser(data).subscribe({
      next: () =>{
        this.isSaving.set(false);
        this.successMessage.set('profile updated successfully.');
      },

      error: (error) => {
        console.error('Faild to update profile', error);
        this.isSaving.set(false);

        if(error.error && typeof error.error === 'object'){
          const firstMessage = Object.values(error.error)[0];
          this.errorMessage.set( (firstMessage as string )|| 'Could not update your profile.');
        }else{
          this.errorMessage.set('Could not update your profile.')
        }
      }
    });
  }

}
