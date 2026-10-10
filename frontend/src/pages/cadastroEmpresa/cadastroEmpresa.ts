import './cadastro-empresa.css'

import { EmpresaLocalStorageService } from '../../services/EmpresaLocalStorageService';
import { CadastroEmpresaController } from '../../controllers/CadastroEmpresaController';

const servicoLocalStorage = new EmpresaLocalStorageService();

// 2. Injeta o serviço no Controller (DIP)
const controller = new CadastroEmpresaController(servicoLocalStorage);

controller.iniciar();