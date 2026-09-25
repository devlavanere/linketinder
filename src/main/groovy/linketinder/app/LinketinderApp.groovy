package linketinder.app

import linketinder.view.MenuPrincipal

println "=== Inicializando o Linketinder (Conectado ao PostgreSQL) ===\n"

// Gerenciador instanciado sem mock
GerenciadorDePerfis gerenciador = new GerenciadorDePerfis()

// Injetando o gerenciador no MenuPrincipal
MenuPrincipal menuPrincipal = new MenuPrincipal(gerenciador)

// Iniciando a interação com o usuário
menuPrincipal.iniciar()


