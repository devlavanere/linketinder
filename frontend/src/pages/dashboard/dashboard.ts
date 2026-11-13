import './dashboard.css'

import { AuthLocalStorageService } from '../../services/AuthLocalStorageService';
import { EmpresaLocalStorageService } from '../../services/EmpresaLocalStorageService';
import { CandidatoLocalStorageService } from '../../services/CandidatoLocalStorageService';
import { VagaLocalStorageService } from '../../services/VagaLocalStorageService';
import { RecrutadorApiService } from '../../services/RecrutadorApiService';
import { DashboardController } from '../../controllers/DashboardController';

const authService = new AuthLocalStorageService();
const empresaService = new EmpresaLocalStorageService();
const candidatoService = new CandidatoLocalStorageService();
const vagaService = new VagaLocalStorageService();
const recrutadorService = new RecrutadorApiService();

// 2. Injeta no Controller (DIP Aplicado)
const controller = new DashboardController(
    authService,
    empresaService,
    candidatoService,
    vagaService,
    recrutadorService
);

controller.iniciar();