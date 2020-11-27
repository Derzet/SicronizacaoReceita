# SicronizacaoReceita

A versão do projeto Java utilizada nesse projeto foi a JDK 11.

Primeiramente realizar o build maven com o código: 

```maven
mvn clean install spring-boot:repackage
```

Depois executar o build com o comando:

```java
java -jar target/processamento-app-1.0-SNAPSHOT.jar "caminhoArquivo"
```


O arquivo com as contas atualizada irá ser gerado dentro do projeto na pasta resources com o nome de arquivo: contaAtualizada.csv

Testes com 1000 threads:
![alt text](images/testes_desempenho_e_unitario_SincronizacaoReceita.png)

Por: Matheus Herminio