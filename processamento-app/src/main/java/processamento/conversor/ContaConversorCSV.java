package processamento.conversor;

import processamento.modelo.ContaReceita;

public final class ContaConversorCSV {

    public static final String CSV_DIVISOR = ";";
    private static final int COLUNA_AGENCIA = 0;
    private static final int COLUNA_CONTA = 1;
    private static final int COLUNA_SALDO = 2;
    private static final int COLUNA_STATUS = 3;

    private ContaConversorCSV(){
    }

    public static double converteTextoNumericoParaDouble(String textoNumerico){
            return Double.valueOf(textoNumerico.replace(',','.'));
    }

    public static String converteDoubleParaTextoNumerico(double valor){
        return String.valueOf(valor).replace('.',',');
    }

    public static ContaReceita converteContaCSVParaContaReceita(String contaCSV){
        String[] lista = contaCSV.split(CSV_DIVISOR);
        return new ContaReceita(lista[COLUNA_AGENCIA], lista[COLUNA_CONTA],
                ContaConversorCSV.converteTextoNumericoParaDouble(lista[COLUNA_SALDO]),lista[COLUNA_STATUS]);
    }

    public static byte[] converteContaReceitaParaBytes(ContaReceita contaReceita){
        String quebraLinha = System.getProperty("line.separator");
        StringBuilder contaCSV = new StringBuilder(stringValueOrEmptyNull(contaReceita.getAgencia()));
        contaCSV.append(stringValueOrEmptyNull(adicionaChar(contaReceita.getConta(),'-',5)));
        contaCSV.append(converteDoubleParaTextoNumerico(contaReceita.getSaldo()).concat(CSV_DIVISOR));
        contaCSV.append(stringValueOrEmptyNull(contaReceita.getStatus()));
        contaCSV.append(converteFlagBooleanParaTexto(contaReceita.getAtualizada()));
        contaCSV.append(quebraLinha);
        return contaCSV.toString().getBytes();
    }

    public static String stringValueOrEmptyNull(String value){
        return value == null? "": value.concat(CSV_DIVISOR);
    }

    private static String converteFlagBooleanParaTexto(Boolean atualizada){
        if(atualizada){
            return "S";
        }else{
            return "N";
        }
    }

    private static String adicionaChar(String str, char ch, int posicao) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(posicao, ch);
        return sb.toString();
    }

}
