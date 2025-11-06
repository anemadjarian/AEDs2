/*
Crie um método iterativo que recebe uma string, sorteia duas letras minúsculas aleatórias (código ASCII >= 'a' e <= 'z'), 
substitui todas as ocorrências da primeira letra na string pela segunda e retorna a string com as alterações efetuadas.

Na saída padrão, para cada linha de entrada, execute o método desenvolvido nesta questão e mostre a string retornada como 
uma linha de saída.

Abaixo, observamos um exemplo de entrada supondo que para a primeira linha as letras sorteadas foram o a e o q. Para a 
segunda linha, foram o e e o k.

Entrada:
o rato roeu a roupa do rei de roma
e qwe qwe qwe ewq ewq ewq
FIM

Saída
o rqto roeu q roupq do rei de romq
k qwk qwk qwk kwq kwq kwq
*/

import java.util.*;
public class AlteracaoAleatoria{
    public static String alteracao(String str, char a, char b){
        String str2 = "";
        for(int i=0; i<str.length(); i++){
            char letra = str.charAt(i);
            if(letra==a){
                str2+=b; //Se a letra for igual a primeira letra randomica, ela modifica para a segunda
            } else{
                str2+=letra; //Se não for, ela continua com a string normal
            }
        }
        return str2;

    }
    public static void main(String[] args){
        String str = MyIO.readLine();
        Random gerador = new Random();
        gerador.setSeed(4);
        while(!(str.length()==3 && str.charAt(0)=='F' && str.charAt(1)=='I' && str.charAt(2)=='M')){
            char a = (char)('a' + (Math.abs(gerador.nextInt()) % 26)); //Gera as duas letras randomicas
            char b = (char)('a' + (Math.abs(gerador.nextInt()) % 26));
            String str2 = alteracao(str, a, b);
            MyIO.println(str2);
            str = MyIO.readLine();
        }
    }
}