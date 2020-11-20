package processamento;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class SincronizacaoReceitaTest {

    private static final long TEMPO_MEDIO_RECEITA_SERVICE_CONTA = 3000;
    private String pathBase;

    @Before
    public void iniciliazaTeste(){
        this.pathBase = System.getProperty("user.dir")+"\\src\\main\\resources\\";
    }

    @Test
    public void atualiza5ContasSucesso() throws IOException, ExecutionException, InterruptedException {
        testePerformanceTempoSicronizacaoReceita("5contasTeste.csv",5);
    }

    @Test
    public void atualiza100ContasSucesso() throws IOException, ExecutionException, InterruptedException {
        testePerformanceTempoSicronizacaoReceita("100contasTeste.csv",100);
    }

    @Test
    public void atualiza1000ContasSucesso() throws IOException, ExecutionException, InterruptedException {
        testePerformanceTempoSicronizacaoReceita("1000contasTeste.csv",1000);
    }

    @Test
    public void atualiza10000ContasSucesso() throws IOException, ExecutionException, InterruptedException {
        testePerformanceTempoSicronizacaoReceita("10000contasTeste.csv",10000);
    }

    private void testePerformanceTempoSicronizacaoReceita(String fileName,long numeroContas) throws IOException, InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        Path caminhoArquivo = SincronizacaoReceita.atualizaContas(pathBase+fileName);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        boolean arquivoExiste = Files.exists(caminhoArquivo);
        Assert.assertEquals(true,arquivoExiste);
        Long tempoMaxPerformar = miliSegundosAceitavel(numeroContas);
        Assert.assertTrue(tempoMaxPerformar>=elapsedTime);
    }


    private long miliSegundosAceitavel(long numeroContas){
        return 30000+((long)((numeroContas*TEMPO_MEDIO_RECEITA_SERVICE_CONTA)/10));
    }


}
