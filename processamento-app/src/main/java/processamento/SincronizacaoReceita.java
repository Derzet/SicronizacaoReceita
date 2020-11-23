package processamento;/*
Cenário de Negócio:
Todo dia útil por volta das 6 horas da manhã um colaborador da retaguarda do Sicredi recebe e organiza as informações de contas para enviar ao Banco Central.
  Todas agencias e cooperativas enviam arquivos Excel à Retaguarda. Hoje o Sicredi já possiu mais de 4 milhões de contas ativas.
Esse usuário da retaguarda exporta manualmente os dados em um arquivo CSV para ser enviada para a Receita Federal, antes as 10:00 da manhã na abertura das agências.

Requisito:
Usar o "serviço da receita" (fake) para processamento automático do arquivo.

Funcionalidade:
0. Criar uma aplicação SprintBoot standalone. Exemplo: java -jar SincronizacaoReceita <input-file>
1. Processa um arquivo CSV de entrada com o formato abaixo.
2. Envia a atualização para a Receita através do serviço (SIMULADO pela classe ReceitaService).
3. Retorna um arquivo com o resultado do envio da atualização da Receita. Mesmo formato adicionando o resultado em uma nova coluna.


Formato CSV:
agencia;conta;saldo;status
0101;12225-6;100,00;A
0101;12226-8;3200,50;A
3202;40011-1;-35,12;I
3202;54001-2;0,00;P
3202;00321-2;34500,00;B
...

atualizado
S N
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import processamento.arquivo.ContaAtualizadaCSVTemplate;
import processamento.arquivo.conversor.ContaConversorCSV;
import processamento.modelo.ContaReceita;
import processamento.servico.externo.ReceitaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootApplication
public class SincronizacaoReceita implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SincronizacaoReceita.class);
    private static final int LINHA_POS = 1;
    private static final String NUMERO_THREADS_PARALELISMO = "1000";

    public static void main(String[] args) {
        LOGGER.info("Inicializando Aplicação!");
        SpringApplication.run(SincronizacaoReceita.class, args);
        LOGGER.info("Finalizando aplicação.");
    }

    @Override
    public void run(String... args) throws Exception {
        /*path gerado encontrado gerado em resources  */
        String path = args[0];
        SincronizacaoReceita.atualizaContas(path);
    }

    public static Path atualizaContas(String path) throws IOException {
        //configurar de acordo com as configuracoes de perfomance a ser obtido
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", NUMERO_THREADS_PARALELISMO);
        String pathBase = System.getProperty("user.dir") + "\\src\\main\\resources\\";
        Path contaAtualizado = Paths.get(pathBase + "contaAtualizada.csv");
        Files.deleteIfExists(contaAtualizado);
        Files.createFile(contaAtualizado);
        List<byte[]> dadosContasAtualizada =
                Files.lines(Paths.get(path))
                        .skip(LINHA_POS)
                        .parallel()
                        .map(SincronizacaoReceita::atualizaLinha)
                        .collect(Collectors.toList());

        return ContaAtualizadaCSVTemplate.criarArquivoContaAtualizada(contaAtualizado, dadosContasAtualizada);
    }


    private static byte[] atualizaLinha(String contaCSV) {
        ContaReceita contaReceita = ContaConversorCSV.converteContaCSVParaContaReceita(contaCSV);
        int numeroMaximoTentativasServico = 5;
        while (numeroMaximoTentativasServico != 0) {
            try {
                ReceitaService receitaService = new ReceitaService();
                Boolean atualizada = receitaService.atualizarConta(contaReceita.getAgencia(),
                        contaReceita.getConta(), contaReceita.getSaldo(), contaReceita.getStatus());
                contaReceita.setAtualizada(atualizada);
                return ContaConversorCSV.converteContaReceitaParaBytes(contaReceita);
            } catch (RuntimeException exception) {
                LOGGER.error("SIRE-1 - ERROR no consumo do serviço ReceitaService. Realizando nova tentativa! ".concat(exception.getClass().getName()));
                numeroMaximoTentativasServico--;
            } catch (InterruptedException exception) {
                LOGGER.error("SIRE-2 - Thread interrompida. Falha na operação de atualização! ".concat(exception.getClass().getName()));
                numeroMaximoTentativasServico--;
                Thread.currentThread().interrupt();
            }
        }
        contaReceita.setAtualizada(Boolean.FALSE);
        return ContaConversorCSV.converteContaReceitaParaBytes(contaReceita);
    }

}
