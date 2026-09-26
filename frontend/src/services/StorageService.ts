import type { ICandidato, IEmpresa, IVaga } from '../models';

export class StorageService{
    // Chave para salvar no navegador
    private readonly CANDIDATOS_KEY = 'linketinder_candidatos';
    private readonly EMPRESAS_KEY = 'linketinder_empresas';
    private readonly VAGAS_KEY = 'linketinder_vaga';
    private readonly SESSION_KEY = 'linketinder_session';

    // Métodos Candidatos

    // A: Método que lê os candidatos salvos
    getCandidatos(): ICandidato[] {
        // Busca texto no banco
        const data = localStorage.getItem(this.CANDIDATOS_KEY);

        // Verificação para ver se tem algo no banco
        if(data) {
            return JSON.parse(data);
        } else {
            // caso vazio
            return [];
        }
    }

    // B: Método que adiciona uym novo candidato
    adicionarCandidato(candidato: ICandidato): void {
        // Chama o método this.getCandidatos() e guarda numa variável
        const lista = this.getCandidatos();

        // Adiciona no array
        lista.push(candidato);

        // Transforma o array em texto e guarda no local storage
        localStorage.setItem(this.CANDIDATOS_KEY, JSON.stringify(lista));
    }

    // Método para deletar candidatos
    deletarCandidato(id: string): void {
        const lista = this.getCandidatos();
        
        // Método filter cria uma nova lista e retira o candidato que tiver o id 
        const novaLista = lista.filter(candidato => candidato.id !== id);

        // Salva lista atualizada
        localStorage.setItem(this.CANDIDATOS_KEY, JSON.stringify(novaLista));
    }

    // Métodos Empresas

    // Método que lê as empresas salvas
    getEmpresas(): IEmpresa[] {
        const data = localStorage.getItem(this.EMPRESAS_KEY);

        if(data) {
            return JSON.parse(data);
        } else {
            return [];
        }
    }

    // Método que adiciona empresa
    adicionarEmpresa(empresa: IEmpresa): void {
        const lista = this.getEmpresas();

        lista.push(empresa);

        localStorage.setItem(this.EMPRESAS_KEY, JSON.stringify(lista));
    }

    // Métodos de Vagas

    getVagas(): IVaga[] {
        const data = localStorage.getItem(this.VAGAS_KEY);

        return data ? JSON.parse(data) : [];
    }

    adicionarVaga(vaga: IVaga): void {
        const lista = this.getVagas();

        lista.push(vaga);

        localStorage.setItem(this.VAGAS_KEY, JSON.stringify(lista));
    }

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

    deletarEmpresa(id: string): void {
        const lista = this.getEmpresas();
        const novaLista = lista.filter(empresa => empresa.id !== id);
        localStorage.setItem(this.EMPRESAS_KEY, JSON.stringify(novaLista));
    }

    deletarVaga(id: string): void {
        const lista = this.getVagas();
        const novaLista = lista.filter(vaga => vaga.id !== id);
        localStorage.setItem(this.VAGAS_KEY, JSON.stringify(novaLista));
    }
}

