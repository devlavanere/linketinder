import type { ICandidato, IEmpresa } from '../models';
import type { ICrudService } from '../services/interfaces/ICrudService';
import type { IAuthService } from '../services/interfaces/IAuthService';

export class LoginController {

    private authService: IAuthService;
    private candidatoService: ICrudService<ICandidato>;
    private empresaService: ICrudService<IEmpresa>;

    constructor(
        authService: IAuthService,
        candidatoService: ICrudService<ICandidato>,
        empresaService: ICrudService<IEmpresa>
    ) {
        this.authService = authService;
        this.candidatoService = candidatoService;
        this.empresaService = empresaService;
    }

    iniciar() {
        const form = document.getElementById('formLogin') as HTMLFormElement;

        if (form) {
            form.addEventListener('submit', (event) => {
                event.preventDefault();
                const email = (document.getElementById('emailLogin') as HTMLInputElement).value;

                const logarCandidato = this.candidatoService.listar().find(c => c.email === email);
                if (logarCandidato) {
                    this.authService.login(logarCandidato.id, 'candidato');
                    window.location.href = '/vagas.html';
                    return;
                }

                const logarEmpresa = this.empresaService.listar().find(e => e.email === email);
                if (logarEmpresa) {
                    this.authService.login(logarEmpresa.id, 'empresa');
                    window.location.href = '/dashboard.html';
                    return;
                }

                alert('E-mail não encontrado. Verifique ou cadastre-se primeiro!');
            });
        }
    }
}