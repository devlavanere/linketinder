package linketinder.dao.interfaces

interface ICrudDAO<T, ID>{
    void inserir(T entidade)
    List<T> listar()
    boolean deletar(ID id)
}