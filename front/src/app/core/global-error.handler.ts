import { ErrorHandler, Injectable } from '@angular/core';
import { NotificationService } from './services/notification.service';
import { AppNotification } from './models/app-notification.interface';

@Injectable()
export class GlobalErrorHandler extends ErrorHandler {

    constructor(private noticationService: NotificationService) {
        super();
    }


    override handleError(error: Error) {
        // Custom error handling logic
        this.noticationService.error(error.message);
        // TODO : should the error be thrown again ?
        // throw error;
    }
}