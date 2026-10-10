import type { IEmpresa } from '../models';
import type { ICrudService } from "./interfaces/ICrudService.ts";

export class EmpresaLocalStorageService implements ICrudService<IEmpresa> {
    private readonly KEY = 'linketinder_empresas';

    listar(): IEmpresa[] {
        const data = localStorage.getItem(this.KEY);
        return data ? JSON.parse(data) : [];
    }

    adicionar(empresa: IEmpresa): void {
        const lista = this.listar();
        lista.push(empresa);
        localStorage.setItem(this.KEY, JSON.stringify(lista));
    }

    deletar(id: string): void {
        const lista = this.listar();
        const novaLista = lista.filter(c => c.id !== id);
        localStorage.setItem(this.KEY, JSON.stringify(novaLista));
    }
}