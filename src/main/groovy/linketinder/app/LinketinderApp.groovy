package linketinder.app

import linketinder.model.Candidato
import linketinder.model.Empresa

println "=== Inicializando o Banco de Dados do Linketinder ===\n"

// Instanciando o Gerenciador de Perfis (substituindo as listas soltas)
GerenciadorDePerfis gerenciador = new GerenciadorDePerfis()

// Mockando os 5 Candidatos (usando o método do gerenciador)
gerenciador.adicionarCandidato(new Candidato(
        nome: "Ana Silva", email: "ana@email.com", estado: "SP", cep: "01000-000",
        descricao: "Desenvolvedora Backend focada em microsserviços.",
        cpf: "111.111.111-11", idade: 25,
        competencias: ["Java", "Spring Framework", "Docker"]
))
gerenciador.adicionarCandidato(new Candidato(
        nome: "Carlos Eduardo", email: "carlos@email.com", estado: "MA", cep: "65000-000",
        descricao: "Full Stack com experiência em arquiteturas web e APIs REST.",
        cpf: "222.222.222-22", idade: 30,
        competencias: ["Python", "Django", "Vue.js"]
))
gerenciador.adicionarCandidato(new Candidato(
        nome: "Beatriz Souza", email: "bia@email.com", estado: "RJ", cep: "20000-000",
        descricao: "Especialista em Frontend e UI/UX.",
        cpf: "333.333.333-33", idade: 28,
        competencias: ["JavaScript", "Angular", "TypeScript"]
))
gerenciador.adicionarCandidato(new Candidato(
        nome: "João Pedro", email: "joao@email.com", estado: "MG", cep: "30000-000",
        descricao: "Engenheiro de Software com foco em sistemas embarcados e baixo nível.",
        cpf: "444.444.444-44", idade: 35,
        competencias: ["C", "C++", "SystemVerilog", "VHDL"]
))
gerenciador.adicionarCandidato(new Candidato(
        nome: "Lucas Mendes", email: "lucas@email.com", estado: "SC", cep: "88000-000",
        descricao: "Entusiasta de automação e scripts de build.",
        cpf: "555.555.555-55", idade: 22,
        competencias: ["Groovy", "Gradle", "Java"]
))

// Mockando as 5 Empresas (usando o método do gerenciador)
gerenciador.adicionarEmpresa(new Empresa(
        nome: "Arroz-Gostoso", email: "rh@arrozgostoso.com.br", estado: "GO", cep: "74000-000",
        descricao: "A maior produtora de arroz do centro-oeste. Buscamos modernizar nossa logística.",
        cnpj: "11.111.111/0001-11", pais: "Brasil",
        competencias: ["Python", "Django", "PostgreSQL"]
))
gerenciador.adicionarEmpresa(new Empresa(
        nome: "Império do Boliche", email: "tech@imperioboliche.com", estado: "SP", cep: "02000-000",
        descricao: "Rede de boliches buscando criar um app de fidelidade inovador.",
        cnpj: "22.222.222/0001-22", pais: "Brasil",
        competencias: ["Vue.js", "JavaScript", "UI/UX"]
))
gerenciador.adicionarEmpresa(new Empresa(
        nome: "Tech Solutions", email: "vagas@techsolutions.com", estado: "SP", cep: "03000-000",
        descricao: "Fábrica de software atendendo clientes no setor financeiro.",
        cnpj: "33.333.333/0001-33", pais: "Brasil",
        competencias: ["Java", "Spring Framework", "Angular"]
))
gerenciador.adicionarEmpresa(new Empresa(
        nome: "Hardware Start", email: "jobs@hardwarestart.com", estado: "RS", cep: "90000-000",
        descricao: "Startup de IoT e automação residencial.",
        cnpj: "44.444.444/0001-44", pais: "Brasil",
        competencias: ["C", "VHDL", "C++"]
))
gerenciador.adicionarEmpresa(new Empresa(
        nome: "Agile Corp", email: "hello@agilecorp.com", estado: "PR", cep: "80000-000",
        descricao: "Consultoria especializada em DevOps e automação de processos.",
        cnpj: "55.555.555/0001-55", pais: "Brasil",
        competencias: ["Groovy", "Docker", "AWS"]
))

// Instanciando o Menu e injetando o gerenciador
Menu menuInterativo = new Menu(gerenciador: gerenciador)

// Iniciando a interação com o usuário
menuInterativo.iniciar()