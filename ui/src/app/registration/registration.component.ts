/**
 * Created by xoll on 08.01.2017.
 */
import {Component} from "@angular/core";
import {MdDialogRef} from "@angular/material";
import {RegistrationUserDTO} from "../models/registration-user.interface";
import {RegistrationUser} from "../models/registration-user.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'registration',
    templateUrl: 'app/registration/registration.template.html',
    styleUrls: ['app/registration/registration.css']
})
export class RegistrationDialog{
    user: RegistrationUserDTO;
    registrationForm: FormGroup;
    formErrors = {
        'username': '',
        'email': '',
        'password': '',
        'passwordConfirm': ''
    };
    validationMessages = {
        'username': {
            'required': 'Username is require'
        },
        'email': {
            'required': 'Email is require',
            'pattern': 'Incorrect email format'
        },
        'password': {
            'required' : 'Password is require'
        },
        'confirmPassword': {
            'required': 'Re-enter password',
            'incorrect': 'Passwords are not equal'
        }
    };

    constructor(private dialogRef: MdDialogRef<RegistrationDialog>,
                private formBuilder: FormBuilder){

        this.user = RegistrationUser.getNewRegistrationUser();
        this.registrationForm = this.formBuilder.group(this.getFormGroup());

        this.registrationForm.valueChanges.subscribe(() => {
            this.onFormChange();
        })
    }

    registration(){
        console.log("Current user " + JSON.stringify(this.user));
    }

    private getFormGroup(){
        return {
            'username': [this.user.username, [Validators.required]],
            'email': [this.user.email, [Validators.required,
                Validators.pattern(/^[a-z0-9!#$%&'*+\/=?^_`{|}~.-]+@[a-z0-9]([a-z0-9-]*[a-z0-9])?(\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*$/i)]],
            'password': [this.user.password, [Validators.required]],
            'confirmPassword': [this.user.passwordConfirm, [Validators.required]]
        }
    }

    private onFormChange(){
        if(!this.registrationForm) return ;
        const form = this.registrationForm;

        for(const field in this.formErrors){
            this.formErrors[field] = '';
            const control = form.get(field);

            if(control && control.dirty && !control.valid){
                const messages = this.validationMessages[field];
                for(const key in control.errors){
                    this.formErrors[field] += messages[key] + " ";
                }
            }
        }
    }

    close(){
        this.dialogRef.close('Cancel');
    }
}