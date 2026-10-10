import './login.css'

import { AuthLocalStorageService } from '../../services/AuthLocalStorageService';
import { CandidatoLocalStorageService } from '../../services/CandidatoLocalStorageService';
import { EmpresaLocalStorageService } from '../../services/EmpresaLocalStorageService';
import { LoginController } from '../../controllers/LoginController';

const authService = new AuthLocalStorageService();
const candidatoService = new CandidatoLocalStorageService();
const empresaService = new EmpresaLocalStorageService();

// 2. Injeta no Controller
const controller = new LoginController(authService, candidatoService, empresaService);

controller.iniciar();