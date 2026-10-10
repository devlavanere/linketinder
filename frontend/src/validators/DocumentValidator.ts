export class DocumentValidator {

    static cpf(cpf: string): boolean {
        const cleanCPF = cpf.replace(/\D/g, '');

        if (cleanCPF.length !== 11 || /^(\d)\1+$/.test(cleanCPF)) return false;

        let sum = 0;
        let rest;

        // Cálculo do primeiro dígito
        for (let i = 1; i <= 9; i++) {
            sum += parseInt(cleanCPF.substring(i - 1, i)) * (11 - i);
        }
        rest = (sum * 10) % 11;
        if (rest === 10 || rest === 11) rest = 0;
        if (rest !== parseInt(cleanCPF.substring(9, 10))) return false;

        sum = 0;
        // Cálculo do segundo dígito
        for (let i = 1; i <= 10; i++) {
            sum += parseInt(cleanCPF.substring(i - 1, i)) * (12 - i);
        }
        rest = (sum * 10) % 11;
        if (rest === 10 || rest === 11) rest = 0;
        return rest === parseInt(cleanCPF.substring(10, 11));
    }

    static cnpj(cnpj: string): boolean {
        const cleanCNPJ = cnpj.replace(/\D/g, '');

        if (cleanCNPJ.length !== 14 || /^(\d)\1+$/.test(cleanCNPJ)) return false;

        const base = cleanCNPJ.substring(0, 12);
        const digits = cleanCNPJ.substring(12);

        // Método auxiliar (SRP e DRY)
        const calc1 = this.calcularDigitoCNPJ(base, 5);
        if (calc1 !== parseInt(digits.charAt(0))) return false;

        const calc2 = this.calcularDigitoCNPJ(base + calc1, 6);
        return calc2 === parseInt(digits.charAt(1));
    }

    // OCP e SRP: Método privado isolado apenas para o cálculo multiplicador do CNPJ
    private static calcularDigitoCNPJ(base: string, pesoInicial: number): number {
        let sum = 0;
        let peso = pesoInicial;

        for (let i = 0; i < base.length; i++) {
            sum += parseInt(base.charAt(i)) * peso;
            peso = peso === 2 ? 9 : peso - 1;
        }

        const resto = sum % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}