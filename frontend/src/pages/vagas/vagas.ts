import './vagas.css'

import { VagaLocalStorageService } from '../../services/VagaLocalStorageService';
import { AuthLocalStorageService } from '../../services/AuthLocalStorageService';
import { CandidatoLocalStorageService } from '../../services/CandidatoLocalStorageService';
import { VagasController } from '../../controllers/VagasController';

const vagaService = new VagaLocalStorageService();
const authService = new AuthLocalStorageService();
const candidatoService = new CandidatoLocalStorageService();

// 2. Injeta todos os serviços necessários no Controller
const controller = new VagasController(vagaService, authService, candidatoService);

controller.iniciar();