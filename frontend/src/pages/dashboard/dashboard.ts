import './dashboard.css'

import { AuthLocalStorageService } from '../../services/AuthLocalStorageService';
import { EmpresaLocalStorageService } from '../../services/EmpresaLocalStorageService';
import { CandidatoLocalStorageService } from '../../services/CandidatoLocalStorageService';
import { VagaLocalStorageService } from '../../services/VagaLocalStorageService';
import { DashboardController } from '../../controllers/DashboardController';

const authService = new AuthLocalStorageService();
const empresaService = new EmpresaLocalStorageService();
const candidatoService = new CandidatoLocalStorageService();
const vagaService = new VagaLocalStorageService();

// 2. Injeta no Controller (DIP Aplicado)
const controller = new DashboardController(
    authService,
    empresaService,
    candidatoService,
    vagaService
);

controller.iniciar();