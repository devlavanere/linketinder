export interface ICrudService<T> {
    listar(): T[];
    adicionar(item: T): void;
    deletar(id: string): void;
}