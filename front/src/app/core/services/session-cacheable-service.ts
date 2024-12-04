import { environment } from '../../../environments/environment';
import { BehaviorSubject } from "rxjs";
import { SessionService } from "./session.service";

export abstract class SessionCacheableService<T> {
    protected cache$: BehaviorSubject<T | null> | null = null;
    protected cachedAt = 0;
    protected isReloading = false;

    constructor(protected sessionService: SessionService) {
        this.sessionService.$isLogged().subscribe(() => {
            this.clearCache();
        })
    }

    /**
     * Clears local cache
     */
    public clearCache(): void {
        this.cache$ = null;
        this.cachedAt = 0;
        this.isReloading = false;
    }

    /**
   * Creates the existing BahaviorSubject used for local caching 
   * @returns the BahaviorSubject 
   */
    protected abstract initCacheSubject(): BehaviorSubject<T | null>;

    /**
   * Creates or gets the existing BahaviorSubject used for local caching 
   * @returns the BahaviorSubject 
   */
    protected getCacheSubject() :BehaviorSubject<T | null> {
        if(this.cache$ === null) {
            this.cache$ = this.initCacheSubject();
        }
        return this.cache$;
    }

    /**
     * 
     * @returns true if reloading needed, false otherwise
     */
    public shouldReload(): boolean {
        return !this.isReloading && new Date().getTime() - this.cachedAt > environment.serviceCacheMaxAgeMs;
    }

    /**
     * Cache has been updated
     */
    protected setCached() :void {
        this.cachedAt = new Date().getTime();
        this.isReloading = false;
    }

    /**
     * Cache is being updated
     */
    protected setReloading() :void {
        this.isReloading = true;
    }
}