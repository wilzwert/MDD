import { ErrorHandler, Injectable } from '@angular/core';
import { NotificationService } from './services/notification.service';
import { AppNotification } from './models/app-notification.interface';

@Injectable()
export class GlobalErrorHandler extends ErrorHandler {

    constructor(private noticationService: NotificationService) {
        console.log('construct');
        super();
    }


    override handleError(error: Error) {
        console.log('handleError '+error.message);
        // Custom error handling logic
        this.noticationService.handleNotification({type: 'error', 'message': error.message} as AppNotification);
        // TODO : should the error be thrown again ?
        // throw error;
    }
}