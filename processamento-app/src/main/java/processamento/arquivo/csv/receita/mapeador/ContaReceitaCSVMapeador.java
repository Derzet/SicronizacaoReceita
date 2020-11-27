package processamento.arquivo.csv.receita.mapeador;

import processamento.arquivo.csv.ArquivoUtilitariosCSV;
import processamento.arquivo.csv.receita.mapeador.exception.DadosReceitaNaoPreenchidoException;
import processamento.externo.receita.modelo.ContaReceita;

public final class ContaReceitaCSVMapeador {

    private static final int COLUNA_AGENCIA = 0;
    private static final int COLUNA_CONTA = 1;
    private static final int COLUNA_SALDO = 2;
    private static final int COLUNA_STATUS = 3;
    private static final int COLUNA_ATUALIZA = 4;


    private ContaReceitaCSVMapeador() {
    }

    public static ContaReceita mapeiaStringContaCSVParaContaReceita(String contaCSV) {
        String[] lista = contaCSV.split(ArquivoUtilitariosCSV.CSV_DIVISOR);
        if(lista.length<=COLUNA_STATUS){
            throw new DadosReceitaNaoPreenchidoException();
        }
        return new ContaReceita(lista[COLUNA_AGENCIA], lista[COLUNA_CONTA],
                ArquivoUtilitariosCSV.converteTextoNumericoParaDouble(lista[COLUNA_SALDO]), lista[COLUNA_STATUS],
                ArquivoUtilitariosCSV.converteTextoParaBoolean(obtemDadoColunaAtualiza(lista)));
    }

    public static String mapeiaContaReceitaParaStringContaCSV(ContaReceita contaReceita) {
        String quebraLinha = ArquivoUtilitariosCSV.obtemSimboloQuebraLinha();
        StringBuilder contaCSV = new StringBuilder(ArquivoUtilitariosCSV.stringValueOrEmptyNull(contaReceita.getAgencia()));
        contaCSV.append(ArquivoUtilitariosCSV.stringValueOrEmptyNull(ArquivoUtilitariosCSV.adicionaChar(contaReceita.getConta(), '-', 5)));
        contaCSV.append(ArquivoUtilitariosCSV.converteDoubleParaTextoNumerico(contaReceita.getSaldo()).concat(ArquivoUtilitariosCSV.CSV_DIVISOR));
        contaCSV.append(ArquivoUtilitariosCSV.stringValueOrEmptyNull(contaReceita.getStatus()));
        contaCSV.append(ArquivoUtilitariosCSV.converteFlagBooleanParaTexto(contaReceita.getAtualizada().booleanValue()));
        contaCSV.append(quebraLinha);
        return contaCSV.toString();
    }

    private static String obtemDadoColunaAtualiza(String[] lista) {
        return lista.length>4?lista[COLUNA_ATUALIZA]:"";
    }

}
