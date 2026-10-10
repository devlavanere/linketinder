import type { IAuthService } from "./interfaces/IAuthService.ts";

export class AuthLocalStorageService implements IAuthService {
    private readonly SESSION_KEY = 'linkedin_service';

    login(id: string, tipo: 'candidato' | 'empresa'): void {
        const session = { id, tipo };
        localStorage.setItem(this.SESSION_KEY, JSON.stringify(session));
    }

    getCurrentUser(): { id: string, tipo: 'candidato' | 'empresa' } | null {
        const data = localStorage.getItem(this.SESSION_KEY);
        return data ? JSON.parse(data) : null;
    }

    logout(): void {
        localStorage.removeItem(this.SESSION_KEY);
    }
}