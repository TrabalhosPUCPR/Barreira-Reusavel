# Barreira-Reusavel
Sistema de barreira simples reusavel no java

<br/>

### ENUNCIADO:

Exercício Barreira Reusável (Barreira de Duas Fases) 
 
Implemente um sistema composto por dois tipos de threads, denominadas 
Trabalhadora e Combinadora, estruturadas da seguinte forma: 
 
Thread Trabalhadora: LOOP: 
1. Cria um arquivo de nome único, contendo um milhão (106) de números inteiros 
aleatórios entre 0 e 107. 
2. Ordena crescentemente os números contidos no arquivo, usando uma 
estrutura de dados em memória (vetor, lista, árvore, etc) que armazene todos 
os números simultaneamente. 
3. Cria um novo arquivo de nome único, contendo os números em ordem 
crescente. 
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
4. Insere o (nome do) arquivo na fila de arquivos gerados. 
5. Sinaliza a thread Combinadora que o novo arquivo está disponível. 
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
 
 
Na inicialização do sistema, são criadas quatro instâncias da thread Trabalhadora. 
 
Os passos 4 e 5 da Thread Trabalhadora compõem o ponto crítico do sistema, isto é, as 
quatro instâncias devem executar esses passos simultaneamente. 
 
Thread Combinadora: LOOP: 
1. Espera a sinalização de quatro novos arquivos disponibilizados pelas quatro 
threads do tipo Trabalhadora. 
2. Remove os quatro arquivos da fila de arquivos gerados. 
3. Faz o merge dos quatro arquivos, eliminando as repetições de números. 
4. Cria um novo arquivo de nome único, contendo o resultado do merge. 
 
Na inicialização do sistema, é criada a única instância da thread Combinadora. 
