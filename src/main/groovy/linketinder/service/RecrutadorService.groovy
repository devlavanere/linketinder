package linketinder.service

import linketinder.dao.interfaces.IRecrutadorDAO
import linketinder.model.Recrutador

import java.time.LocalDate
import java.time.Period

class RecrutadorService {
    private IRecrutadorDAO dao

    RecrutadorService(IRecrutadorDAO dao) {
        this.dao = dao
    }

    void cadastrar(Recrutador r) {
        validarEmail(r.email)
        validarCpf(r.cpf)
        validarDataNascimento(r.dataNascimento)
        validarIdade(r)
        validarSenha(r.senha)
        validarUnicidade(r)

        dao.inserir(r)
    }

    List<Recrutador> listar() {
        return dao.listar()
    }

    List<Recrutador> listarPorEmpresa(Integer idEmpresa) {
        if (!idEmpresa || idEmpresa <= 0) {
            throw new IllegalArgumentException("ID da empresa inválido.")
        }
        return dao.listarPorEmpresa(idEmpresa)
    }

    void deletar(Integer id) {
        if (!id || id <= 0) {
            throw new IllegalArgumentException("ID do recrutador inválido.")
        }
        boolean sucesso = dao.deletar(id)
        if (!sucesso) {
            throw new RuntimeException("Nenhum recrutador encontrado com o ID informado.")
        }
    }

    private void validarEmail(String email) {
        if (!email || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail não pode ser vazio.")
        }
        String regex = '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'
        if (!email.matches(regex)) {
            throw new IllegalArgumentException("E-mail inválido. Formato esperado: nome@dominio.com")
        }
    }

    private void validarCpf(String cpf) {
        if (!cpf || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio.")
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "")

        if (cpfLimpo.length() != 11) {
            throw new IllegalArgumentException("CPF inválido. Deve conter exatamente 11 dígitos.")
        }

        if (cpfLimpo.matches('(.)\\1{10}')) {
            throw new IllegalArgumentException("CPF inválido. CPFs com todos os dígitos iguais não são aceitos.")
        }

        if (!validarDigitosVerificadoresCpf(cpfLimpo)) {
            throw new IllegalArgumentException("CPF inválido. Dígitos verificadores não conferem.")
        }
    }

    private boolean validarDigitosVerificadoresCpf(String cpf) {
        int[] digitos = cpf.collect { Character.getNumericValue(it as char) } as int[]

        int soma1 = 0
        for (int i = 0; i < 9; i++) {
            soma1 += digitos[i] * (10 - i)
        }
        int resto1 = soma1 % 11
        int d1 = (resto1 < 2) ? 0 : 11 - resto1

        if (d1 != digitos[9]) {
            return false
        }

        int soma2 = 0
        for (int i = 0; i < 10; i++) {
            soma2 += digitos[i] * (11 - i)
        }
        int resto2 = soma2 % 11
        int d2 = (resto2 < 2) ? 0 : 11 - resto2

        return d2 == digitos[10]
    }

    private void validarDataNascimento(LocalDate dataNascimento) {
        if (!dataNascimento) {
            throw new IllegalArgumentException("Data de nascimento não pode ser nula.")
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser uma data futura.")
        }
    }

    private void validarIdade(Recrutador r) {
        int idade = r.getIdade()
        if (idade < 18) {
            throw new IllegalArgumentException("O recrutador deve ter pelo menos 18 anos para se cadastrar.")
        }
    }

    private void validarSenha(String senha) {
        if (!senha || senha.length() < 8) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres.")
        }
        if (!senha.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos uma letra maiúscula.")
        }
        if (!senha.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos uma letra minúscula.")
        }
        if (!senha.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos um número.")
        }
        if (!senha.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException("Senha deve conter pelo menos um caractere especial.")
        }
    }

    private void validarUnicidade(Recrutador r) {
        if (dao.emailJaEmUso(r.email)) {
            throw new IllegalArgumentException("E-mail já está em uso por outro candidato, empresa ou recrutador.")
        }
        if (dao.cpfJaEmUso(r.cpf)) {
            throw new IllegalArgumentException("CPF já está em uso por outro candidato ou recrutador.")
        }
    }
}
