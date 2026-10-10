import type { IVaga } from "../models";
import type { ICrudService } from "./interfaces/ICrudService.ts";

export class VagaLocalStorageService implements ICrudService<IVaga> {
    private readonly KEY = 'linketinder_vaga';

    listar(): IVaga[] {
        const data = localStorage.getItem(this.KEY);
        return data ? JSON.parse(data) : [];
    }

    adicionar(candidato: IVaga): void {
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

