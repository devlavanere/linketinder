export class RegexValidator {
    // Aceita letras e caracteres acentuados via Unicode, exigindo nome e sobrenome
    static nome(valor: string): boolean {
        return /^[A-Za-z\u00C0-\u00FC]+(?:\s[A-Za-z\u00C0-\u00FC]+)+$/.test(valor.trim());
    }

    // Validação com obrigatoriedade de ponto no domínio
    static email(valor: string): boolean {
        return /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(valor.trim());
    }

    // Formato padrão 00000-000
    static cep(valor: string): boolean {
        return /^\d{5}-\d{3}$/.test(valor.trim());
    }

    // Aceita palavras, acentos, C#, C++, hifens e espaços, separados obrigatoriamente por vírgula
    static tags(valor: string): boolean {
        return /^[\w\u00C0-\u00FC#+\-\s]+(,[\w\u00C0-\u00FC#+\-\s]+)*$/.test(valor.trim());
    }
}