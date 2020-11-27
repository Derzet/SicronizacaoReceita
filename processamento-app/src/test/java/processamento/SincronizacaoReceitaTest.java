package processamento;


import org.junit.Before;
import org.junit.Test;
import processamento.arquivo.csv.ArquivoUtilitariosCSV;
import processamento.arquivo.csv.receita.mapeador.ContaReceitaCSVMapeador;
import processamento.arquivo.csv.receita.mapeador.exception.DadosReceitaNaoPreenchidoException;
import processamento.externo.receita.modelo.ContaReceita;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SincronizacaoReceitaTest {

    private static final long TEMPO_BASE = 5000;
    private static final long NUMERO_CONTAS_SICREDI = 4000000;
    private static final long TEMPO_MAXIMO_PROCESSAMENTO_MILISSEGUNDOS = 144000000;
    private String pastaBase;
    private String separadorArquivo;


    @Before
    public void iniciliazaTeste() {
        setPastaBase();
    }

    @Test
    public void atualiza5ContasSucesso() throws IOException {
        testePerformanceTempoSicronizacaoReceita("5contasTeste.csv", 5);
    }

    @Test
    public void atualiza100ContasSucesso() throws IOException {
        testePerformanceTempoSicronizacaoReceita("100contasTeste.csv", 100);
    }

    @Test
    public void atualiza1000ContasSucesso() throws IOException {
        testePerformanceTempoSicronizacaoReceita("1000contasTeste.csv", 1000);
    }

    @Test
    public void atualiza10000ContasSucesso() throws IOException {
        testePerformanceTempoSicronizacaoReceita("10000contasTeste.csv", 10000);
    }

    @Test
    public void atualiza100000ContasSucesso() throws IOException {
        testePerformanceTempoSicronizacaoReceita("100000contasTeste.csv", 100000);
    }

    @Test(expected = NoSuchFileException.class)
    public void arquivoNaoExiste() throws IOException {
        SincronizacaoReceita.atualizaContas(criaCaminhoArquivoOrigem("arquivoInexistente.csv"));
    }

    @Test
    public void formatoInvalidoArquivoExtensao() {
        nomeArquivoInvalido("extensaoInvalida.doc");
    }

    @Test
    public void formatoInvalidoArquivoSemExtensao() {
        nomeArquivoInvalido("semExtensao");
    }

    @Test(expected = InvalidParameterException.class)
    public void deveriaSomenteAceitarContaPadraoNumerico() throws IOException {
        SincronizacaoReceita.atualizaContas(criaCaminhoArquivoOrigem("contaTextoTeste.csv"));
    }

    @Test(expected = DadosReceitaNaoPreenchidoException.class)
    public void deveriaIndicarExcecaoQuandoDadosReceitaNaoForemPreenchido() throws IOException {
        SincronizacaoReceita.atualizaContas(criaCaminhoArquivoOrigem("dadosNaoPreenchidosTeste.csv"));
    }

    @Test
    public void deveriaIntegridadeDadosEntradaPermaneceremNoArquivoSaidaEAtualizar() throws IOException {
        final String nomeArquivo = "receitaTesteIntegridadeDadosTeste.csv";
        File arquivoSaida = SincronizacaoReceita.atualizaContas(criaCaminhoArquivoOrigem(nomeArquivo));
        List<ContaReceita> dadosEntrada = obtemDadosCSVContaReceita(criaCaminhoArquivoOrigem(nomeArquivo));
        List<ContaReceita> dadosSaida = obtemDadosCSVContaReceita(arquivoSaida);
        assertEquals(dadosEntrada.size(), dadosSaida.size());
        checaIntegridadeArquivoSaida(dadosEntrada, dadosSaida);
    }

    public void checaIntegridadeContasCSV(ContaReceita contaReceitaEntrada, ContaReceita contaReceitaSaida) {
        assertEquals(contaReceitaEntrada.getAgencia(), contaReceitaSaida.getAgencia());
        assertEquals(contaReceitaEntrada.getConta(), contaReceitaSaida.getConta());
        assertEquals(contaReceitaEntrada.getSaldo(), contaReceitaSaida.getSaldo(), 0);
        assertEquals(contaReceitaEntrada.getStatus(), contaReceitaSaida.getStatus());
        assertNull(contaReceitaEntrada.getAtualizada());
        assertNotNull(contaReceitaSaida.getAtualizada());
    }

    private void checaIntegridadeArquivoSaida(List<ContaReceita> dadosEntrada, List<ContaReceita> dadosSaida) {
        for (int contador = 0; contador < dadosEntrada.size(); contador++) {
            ContaReceita contaReceitaEntrada = dadosEntrada.get(contador);
            ContaReceita contaReceitaSaida = dadosSaida.get(contador);
            assertNotNull(contaReceitaEntrada);
            assertNotNull(contaReceitaSaida);
            checaIntegridadeContasCSV(contaReceitaEntrada, contaReceitaSaida);
        }
    }

    private void nomeArquivoInvalido(String nomeArquivo) {
        String message = null;
        try {
            SincronizacaoReceita.atualizaContas(criaCaminhoArquivoOrigem(nomeArquivo));
        } catch (IOException ioException) {
            message = ioException.getMessage();
        }
        assertNotNull(message);
        assertEquals(ArquivoUtilitariosCSV.ERRO_EXTENSAO_FORMATO_INVALIDO_PARA_CSV, message);
    }

    private List<ContaReceita> obtemDadosCSVContaReceita(File caminhoArquivo) throws IOException {
        return ArquivoUtilitariosCSV.carregaDadosArquivo(caminhoArquivo)
                .stream()
                .map(ContaReceitaCSVMapeador::mapeiaStringContaCSVParaContaReceita)
                .collect(Collectors.toList());
    }


    private void testePerformanceTempoSicronizacaoReceita(String nomeArquivo, long numeroContas) throws IOException {
        long tempoInicio = System.currentTimeMillis();
        File arquivo = SincronizacaoReceita.atualizaContas(criaCaminhoArquivoOrigem(nomeArquivo));
        long tempoFim = System.currentTimeMillis();
        long tempoPassado = tempoFim - tempoInicio;
        boolean arquivoExiste = Files.exists(arquivo.toPath());
        assertEquals(true, arquivoExiste);
        Long tempoMaximoProcessamentoMilissegundos = obtemTempoMaximoProcessamentoMilissegundos(numeroContas);
        assertTrue(tempoMaximoProcessamentoMilissegundos >= tempoPassado);
    }


    private long obtemTempoMaximoProcessamentoMilissegundos(long numeroContas) {
        return TEMPO_BASE + (numeroContas * TEMPO_MAXIMO_PROCESSAMENTO_MILISSEGUNDOS) / NUMERO_CONTAS_SICREDI;
    }

    private void setPastaBase() {
        this.separadorArquivo = File.separator;
        this.pastaBase = System.getProperty("user.dir") + separadorArquivo;
        concatenaDiretorio("src");
        concatenaDiretorio("test");
        concatenaDiretorio("resources");
        concatenaDiretorio("sincronizacaoReceita");
        concatenaDiretorio("arquivo");
        concatenaDiretorio("entrada");

    }

    private void concatenaDiretorio(String string) {
        this.pastaBase = pastaBase + string + separadorArquivo;
    }

    private File criaCaminhoArquivoOrigem(String nomeArquivo) {
        return new File(this.pastaBase + nomeArquivo);
    }
}
