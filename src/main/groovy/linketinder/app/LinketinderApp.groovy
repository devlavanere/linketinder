package linketinder.app

import linketinder.view.MenuPrincipal

println "=== Inicializando o Linketinder (Conectado ao PostgreSQL) ===\n"

GerenciadorDePerfis gerenciador = new GerenciadorDePerfis()

// Injetando o gerenciador no MenuPrincipal
MenuPrincipal menuPrincipal = new MenuPrincipal(gerenciador)

menuPrincipal.iniciar()


