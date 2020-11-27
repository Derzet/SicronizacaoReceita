package processamento.arquivo.csv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public final class ArquivoUtilitariosCSV {
    private static final int LINHA_CABECARIO = 1;
    public static final String CSV_DIVISOR = ";";
    public static final String ERRO_EXTENSAO_FORMATO_INVALIDO_PARA_CSV = "Extensão com formato inválido. Requer extensão do tipo .csv !";

    private ArquivoUtilitariosCSV() {
    }

    public static List<String> carregaDadosArquivo(File caminhoDiretorio) throws IOException {
        if (ArquivoUtilitariosCSV.possuiExtensaoCSV(caminhoDiretorio.getAbsolutePath())) {
            return Files.lines(Paths.get(caminhoDiretorio.getAbsolutePath()))
                    .skip(LINHA_CABECARIO).collect(Collectors.toList());
        } else {
            throw new IOException(ERRO_EXTENSAO_FORMATO_INVALIDO_PARA_CSV);
        }
    }

    public static boolean possuiExtensaoCSV(String caminhoArquivo) {
        return obtemExtensao(caminhoArquivo).equals("csv");
    }

    public static String obtemExtensao(String caminhoArquivo) {
        String extensao = "";
        int i = caminhoArquivo.lastIndexOf('.');
        if (i > 0) {
            extensao = caminhoArquivo.substring(i + 1);
        }
        return extensao;
    }

    public static String stringValueOrEmptyNull(String value) {
        return value == null ? "" : value.concat(CSV_DIVISOR);
    }


    public static double converteTextoNumericoParaDouble(String textoNumerico) {
        return Double.valueOf(textoNumerico.replace(',', '.'));
    }

    public static String converteFlagBooleanParaTexto(boolean atualizada) {
        if (atualizada) {
            return "S";
        } else {
            return "N";
        }
    }

    public static Boolean converteTextoParaBoolean(String texto) {
        if (texto.equals("S")) {
            return Boolean.TRUE;
        } else if (texto.equals("N")) {
            return Boolean.FALSE;
        }
        return null;
    }

    public static String adicionaChar(String str, char ch, int posicao) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(posicao, ch);
        return sb.toString();
    }

    public static String converteDoubleParaTextoNumerico(double valor) {
        return String.format("%.2f", valor).replace('.', ',');
    }

    public static String obtemSimboloQuebraLinha() {
        return System.getProperty("line.separator");
    }

}
