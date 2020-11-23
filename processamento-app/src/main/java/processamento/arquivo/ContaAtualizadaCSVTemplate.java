package processamento.arquivo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ContaAtualizadaCSVTemplate {
    private static final String CABECARIO_CONTA_ATUALIZADO = "agencia;conta;saldo;status;atualizado";

    private ContaAtualizadaCSVTemplate() {
    }

    public static Path criarArquivoContaAtualizada(Path contaAtualizado, List<byte[]> dados) throws IOException {
        Files.write(contaAtualizado, (CABECARIO_CONTA_ATUALIZADO + "\r\n").getBytes(), StandardOpenOption.WRITE);
        for (byte[] contaCSV : dados) {
            Files.write(contaAtualizado, contaCSV, StandardOpenOption.APPEND);
        }
        return contaAtualizado;
    }

}
