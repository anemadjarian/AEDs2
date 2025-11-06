import java.util.*;

//NÃO FINALIZADO

/*
Ranking de Jogos

Você está criando um sistema de ranking para jogos.
Cada jogo tem um nome e uma pontuação.

Insira 5 jogos (nome e pontuação) em um vetor.

Implemente uma função de inserção ordenada: quando o jogador é adicionado, ele já entra na posição correta (do maior para o menor).

Depois, implemente uma remoção: dado o nome do jogo, remova-o do ranking.

Mostre o ranking atualizado.

🧠 Desafio extra: se dois jogos tiverem a mesma pontuação, ordene por ordem alfabética do nome.
 */

public class Ranking {
    String nome;
    float pontuacao;

    public Ranking(){
        this.nome="";
        this.pontuacao=-1;
    }

    public Ranking(String nome, float pontucao){
        this.nome=nome;
        this.pontuacao=pontuacao;
    }

    //-----ORDENAÇÃO-----
    void quickSort(Ranking[] array, int primeiro, int ultimo){
        int i=primeiro;
        int j=ultimo;
        int pivo=(primeiro+ultimo)/2;

        while(i<=j){
            while (array[i].pontuacao < array[pivo].pontuacao) i++;
            while(array[j].pontuacao > array[pivo].pontuacao) j--;
            if(i<=j){
                Ranking aux = array[i];
                array[i] = array[j];
                array[j] = aux;
                i++;
                j--;
            }
        }
        if(primeiro < j) quickSort(array, primeiro, j);
        if(i < ultimo) quickSort(array, i, ultimo);
    }

    int insercao(Ranking[] array, String nome, float pontuacao, int n) throws Exception{
        if(n==4){
            throw new Exception("Erro");
        }
        array[n].nome=nome;
        array[n].pontuacao=pontuacao;
        quickSort(array, 0, n);
        n++;
        return n;
    }

    int remocao(Ranking[] array, int n, int pos)throws Exception{
        if(n==0 || pos>n){
            throw new Exception("Erro");
        }
        System.out.println("Foi removido: " + array[pos].nome + ", com pontuação:" + array[pos].pontuacao);
        for(int i=pos; i<n; i++){
            array[i]=array[i+1];
        }
        n--;
        return n;
    }

    void mostrar(Ranking array, int n){
        
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        Ranking[] array = new Ranking[5];
        int n=0;
        System.out.println("É permitido a inserção de 5 jogos e remoção dos mesmos");
        int resp=0;
        while (resp!=4) {
            System.out.println("1- Inserir");
            System.out.println("2- Remover");
            System.out.println("3- Mostrar");
            System.out.println("4- Sair");
            resp=sc.nextInt();

            switch (resp) {
                case 1:
                    System.out.println("Digite o nome do jogo:");
                    String nome = sc.nextLine();
                    System.out.println("Digite a pontuação do jogo:");
                    float pontuacao = sc.nextFloat();
                    n = insercao(array, nome, pontuacao, n);
                    break;
                
                case 2:
                    System.out.println("Você deseja remover o jogo de qual posição?");
                    int pos = sc.nextInt();
                    n = remocao(array, n, pos);
                default:
                    break;
            }
        }
    }
}