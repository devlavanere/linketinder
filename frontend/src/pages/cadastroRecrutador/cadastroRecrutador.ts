import './cadastro-recrutador.css'

import { RecrutadorApiService } from '../../services/RecrutadorApiService';
import { CadastroRecrutadorController } from '../../controllers/CadastroRecrutadorController';

const servicoApi = new RecrutadorApiService();

// Injeta o serviço no Controller (DIP - Inversão de Dependência)
const controller = new CadastroRecrutadorController(servicoApi);

controller.iniciar();
