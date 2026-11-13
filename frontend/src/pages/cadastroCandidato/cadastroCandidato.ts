import './cadastro-candidato.css'

import { CandidatoApiService } from '../../services/CandidatoApiService.ts';
import { CadastroCandidatoController } from '../../controllers/CadastroCandidatoController';

const servicoApi = new CandidatoApiService;

// Injeta o serviço no Controller (DIP - Inversão de Dependência)
const controller = new CadastroCandidatoController(servicoApi);

controller.iniciar();