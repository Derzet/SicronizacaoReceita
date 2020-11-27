package processamento.arquivo.csv.receita;

import processamento.arquivo.csv.ArquivoUtilitariosCSV;
import processamento.arquivo.csv.receita.mapeador.ContaReceitaCSVMapeador;
import processamento.externo.receita.modelo.ContaReceita;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ContaAtualizadaTemplateCSV {
    private static final String CABECARIO_CONTA_ATUALIZADO = "agencia;conta;saldo;status;atualizado";
    private File arquivo;
    private  List<ContaReceita> dadosContaCSV;

    public ContaAtualizadaTemplateCSV(File arquivoOrigem,List<ContaReceita> dadosContaCSV) throws IOException {
        setCaminhoArquivo(arquivoOrigem);
        this.dadosContaCSV = dadosContaCSV;
        criarArquivoContaAtualizada();
    }

    private void criarArquivoContaAtualizada() throws IOException {
        Path diretorioTemp = this.arquivo.toPath();
        Files.deleteIfExists(diretorioTemp);
        Files.createFile(diretorioTemp);
        Files.write(diretorioTemp, (CABECARIO_CONTA_ATUALIZADO + ArquivoUtilitariosCSV.obtemSimboloQuebraLinha()).getBytes(), StandardOpenOption.WRITE);
        for (ContaReceita contaReceita : dadosContaCSV) {
            Files.write(diretorioTemp, ContaReceitaCSVMapeador.mapeiaContaReceitaParaStringContaCSV(contaReceita).getBytes(), StandardOpenOption.APPEND);
        }
    }

    public File getArquivo() {
        return arquivo;
    }

    private void setCaminhoArquivo(File caminhoArquivoOrigem) {
        StringBuilder caminhoArquivoTemp =  new StringBuilder(System.getProperty("user.dir").concat(File.separator));
        concatenaDiretorio(caminhoArquivoTemp,"src");
        concatenaDiretorio(caminhoArquivoTemp,"test");
        concatenaDiretorio(caminhoArquivoTemp,"resources");
        concatenaDiretorio(caminhoArquivoTemp,"sincronizacaoReceita");
        concatenaDiretorio(caminhoArquivoTemp,"arquivo");
        concatenaDiretorio(caminhoArquivoTemp,"saida");
        concatenaDiretorio(caminhoArquivoTemp,caminhoArquivoOrigem.getName());
        this.arquivo = new File(caminhoArquivoTemp.toString());

    }

    private void concatenaDiretorio(StringBuilder stringBuilder,String string){
        stringBuilder.append(string);
        stringBuilder.append(File.separator);
    }

}
