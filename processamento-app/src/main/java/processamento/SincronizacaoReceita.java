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
import processamento.arquivo.csv.ArquivoUtilitariosCSV;
import processamento.arquivo.csv.receita.ContaAtualizadaTemplateCSV;
import processamento.arquivo.csv.receita.mapeador.ContaReceitaCSVMapeador;
import processamento.externo.receita.modelo.ContaReceita;
import processamento.externo.receita.servico.ReceitaService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;


@SpringBootApplication
public class SincronizacaoReceita implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SincronizacaoReceita.class);

    public static void main(String[] args) {
        LOGGER.info("Inicializando Aplicação!");
        SpringApplication.run(SincronizacaoReceita.class, args);
        LOGGER.info("Finalizando aplicação.");
    }

    @Override
    public void run(String... args) throws Exception {
        /*path gerado encontrado gerado em resources  */
        SincronizacaoReceita.atualizaContas(new File(args[0]));
    }

    public static File atualizaContas(File caminhoArquivoOrigem) throws IOException {
        List<ContaReceita> dadosContasAtualizada = carregaDadosContaReceitaAtualizaCSV(caminhoArquivoOrigem);
        ContaAtualizadaTemplateCSV contaAtualizadaTemplateCSV = new ContaAtualizadaTemplateCSV(caminhoArquivoOrigem,dadosContasAtualizada);
        return contaAtualizadaTemplateCSV.getArquivo();
    }

    private static List<ContaReceita> carregaDadosContaReceitaAtualizaCSV(File caminhoArquivo) throws IOException {
        //configurar o numero de threads de acordo com as configuracoes de performance
        ForkJoinPool customizadaPool = new ForkJoinPool(1000);
        List<String> dadosContaCSV = ArquivoUtilitariosCSV.carregaDadosArquivo(caminhoArquivo);
        List<ContaReceita> contaReceitas =  customizadaPool.submit(() ->

            dadosContaCSV.stream()
                    .parallel()
                    .map(SincronizacaoReceita::atualizaContaReceita)
                    .collect(Collectors.toList())
        ).join();
        customizadaPool.shutdown();
        return contaReceitas;
    }

    private static ContaReceita atualizaContaReceita(String contaCSV) {
        ContaReceita contaReceita = ContaReceitaCSVMapeador.mapeiaStringContaCSVParaContaReceita(contaCSV);
        int numeroMaximoTentativasServico = 5;
        while (numeroMaximoTentativasServico != 0) {
            try {
                ReceitaService receitaService = new ReceitaService();
                Boolean atualizada = receitaService.atualizarConta(contaReceita.getAgencia(),
                        contaReceita.getConta(), contaReceita.getSaldo(), contaReceita.getStatus());
                contaReceita.setAtualizada(atualizada);
                return contaReceita;
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
        return contaReceita;
    }

}
