import type { ICandidato } from '../models';
import type { ICrudService } from './interfaces/ICrudService';

export class CandidatoLocalStorageService implements ICrudService<ICandidato> {
    private readonly KEY = 'linketinder_candidatos';

    listar(): ICandidato[] {
        const data = localStorage.getItem(this.KEY);
        return data ? JSON.parse(data) : [];
    }

    adicionar(candidato: ICandidato): void {
        const lista = this.listar();
        lista.push(candidato);
        localStorage.setItem(this.KEY, JSON.stringify(lista));
    }

    deletar(id: string): void {
        const lista = this.listar();
        const novaLista = lista.filter(c => c.id !== id);
        localStorage.setItem(this.KEY, JSON.stringify(novaLista));
    }
}