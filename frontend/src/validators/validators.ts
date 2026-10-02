// src/validators/validators.ts

export const RegexValidators = {
    // 1. Validações de Formato (Expressões Regulares)
    
    // Aceita letras e caracteres acentuados via Unicode, exigindo nome e sobrenome
    nome: (valor: string) => /^[A-Za-z\u00C0-\u00FC]+(?:\s[A-Za-z\u00C0-\u00FC]+)+$/.test(valor.trim()),
    
    // Validação com obrigatoriedade de ponto no domínio
    email: (valor: string) => /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(valor.trim()),
    
    // Formato padrão 00000-000
    cep: (valor: string) => /^\d{5}-\d{3}$/.test(valor.trim()),
    
    // Aceita palavras, acentos, C#, C++, hifens e espaços, separados obrigatoriamente por vírgula
    tags: (valor: string) => /^[\w\u00C0-\u00FC#+\-\s]+(,[\w\u00C0-\u00FC#+\-\s]+)*$/.test(valor.trim()),

    // 2. Validações Algorítmicas (Matemática com limpeza via Regex)
    
    cpf: (cpf: string) => {
        const cleanCPF = cpf.replace(/\D/g, ''); // Limpa a máscara deixando só números
        
        if (cleanCPF.length !== 11 || /^(\d)\1+$/.test(cleanCPF)) return false;

        let sum = 0, rest;
        for (let i = 1; i <= 9; i++) sum += parseInt(cleanCPF.substring(i - 1, i)) * (11 - i);
        rest = (sum * 10) % 11;
        if ((rest === 10) || (rest === 11)) rest = 0;
        if (rest !== parseInt(cleanCPF.substring(9, 10))) return false;

        sum = 0;
        for (let i = 1; i <= 10; i++) sum += parseInt(cleanCPF.substring(i - 1, i)) * (12 - i);
        rest = (sum * 10) % 11;
        if ((rest === 10) || (rest === 11)) rest = 0;
        return rest === parseInt(cleanCPF.substring(10, 11));
    },

    cnpj: (cnpj: string) => {
        const cleanCNPJ = cnpj.replace(/\D/g, ''); 
        
        if (cleanCNPJ.length !== 14 || /^(\d)\1+$/.test(cleanCNPJ)) return false;

        let size = cleanCNPJ.length - 2;
        let numbers = cleanCNPJ.substring(0, size);
        const digits = cleanCNPJ.substring(size);
        let sum = 0;
        let pos = size - 7;
        
        for (let i = size; i >= 1; i--) {
            sum += parseInt(numbers.charAt(size - i)) * pos--;
            if (pos < 2) pos = 9;
        }
        let result = sum % 11 < 2 ? 0 : 11 - (sum % 11);
        if (result !== parseInt(digits.charAt(0))) return false;

        size = size + 1;
        numbers = cleanCNPJ.substring(0, size);
        sum = 0;
        pos = size - 7;
        
        for (let i = size; i >= 1; i--) {
            sum += parseInt(numbers.charAt(size - i)) * pos--;
            if (pos < 2) pos = 9;
        }
        result = sum % 11 < 2 ? 0 : 11 - (sum % 11);
        return result === parseInt(digits.charAt(1));
    }
};