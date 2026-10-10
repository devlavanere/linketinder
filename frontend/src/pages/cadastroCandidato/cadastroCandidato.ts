import './cadastro-candidato.css'

import { CandidatoLocalStorageService } from '../../services/CandidatoLocalStorageService';
import { CadastroCandidatoController } from '../../controllers/CadastroCandidatoController';

const servicoLocalStorage = new CandidatoLocalStorageService();

// Injeta o serviço no Controller (DIP - Inversão de Dependência)
const controller = new CadastroCandidatoController(servicoLocalStorage);

controller.iniciar();