import type { ICandidato, IEmpresa } from '../models';

export class StorageService{
    // Chave para salvar no navegador
    private readonly CANDIDATOS_KEY = 'linketinder_candidatos';
    private readonly EMPRESAS_KEY = 'linketinder_empresas';

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
}

