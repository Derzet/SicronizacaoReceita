# SicronizacaoReceita
sicronizacaoReceita

Primeiramente realizar o build maven com o código: mvn clean install spring-boot:repackage
Depois executar o build:
java -jar target/processamento-app-1.0-SNAPSHOT.jar "caminhoArquivo"

O arquivo irá ser gerado em resources com o nome: contaAtualizada.csv
