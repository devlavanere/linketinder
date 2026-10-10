export interface IAuthService {
    login(id: string, tipo: 'candidato' | 'empresa'): void;
    getCurrentUser(): { id: string, tipo: 'candidato' | 'empresa' } | null;
    logout(): void;
}