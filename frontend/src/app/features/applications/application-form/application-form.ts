import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApplicationService } from '../../../core/services/application.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardHeader, MatCard, MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { validate } from '@angular/forms/signals';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-application-form',
  standalone: true,
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule, MatIcon],
  templateUrl: './application-form.html',
  styleUrl: './application-form.scss',
})
export class ApplicationForm implements OnInit{
  private fb = inject(FormBuilder);
  private applicationService = inject(ApplicationService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  isEditMode = false;
  applicationId: number | null = null;
  isSaving = false;
  errorMessage = '';
  maxDate = new Date();

  StatusOptions = ['APPLIED', 'INTERVIEW', 'OFFER', 'REJECTED'];

  applicationForm = this.fb.group({
    company:['',Validators.required],
    role:['',Validators.required],
    status:['APPLIED',Validators.required],
    location:['', Validators.required],
    dateApplied:['',Validators.required],
    notes:['', Validators.maxLength(100)]

  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if(idParam){
      this.isEditMode = true;
      this.applicationId= Number(idParam);
      this.loadApplication(this.applicationId);
    }
  }


  private loadApplication(id: number): void{
    this.applicationService.getById(id).subscribe({
      next: (application) => {
        this.applicationForm.patchValue({
          company: application.company,
          role: application.role,
          status: application.status,
          location:application.location,
          dateApplied: application.dateApplied,
          notes: application.notes
        });
      }, 
      error: (error) => {
        console.error('Failed to load application', error );
        this.errorMessage = 'Coulde not load this application.';
      }
    });
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth()+1).padStart(2,'0');
    const day = String(date.getDate()).padStart(2,'0');

    return `${year}-${month}-${day}`;
  }

  onSubmit(): void {
    if(this.applicationForm.invalid){
      this.applicationForm.markAllAsTouched();
      return;
    }
    this.isSaving = true;
    this.errorMessage = '';

    const formValue = this.applicationForm.value ;
    const rowDateApplied = formValue.dateApplied as any;
    const payload = {
       ...formValue,
       dateApplied: rowDateApplied instanceof Date 
          ? this.formatDate(rowDateApplied)
          : rowDateApplied
    };

    const request = this.isEditMode && this.applicationId
    ? this.applicationService.update(this.applicationId, payload as any)
    : this.applicationService.create(payload as any);

    request.subscribe({
      next:() => {
        this.isSaving = false;
        this.router.navigate(['/applicationList']);
      }, 
      error: (error) => {
        this.isSaving = false;
        console.error('Failed to save application', error);
        if(error.error && typeof error.error === 'object'){
          const firstMessage = Object.values(error.error)[0];
          this.errorMessage = (firstMessage as string) || 'Could not save application. Please try agian.';
        }else{
          this.errorMessage = 'Could not save this application. please try agian';
        }
        
      }
    });
  }
    onCancel(): void{
      this.router.navigate(['/applicationList'])
    

  }




}
