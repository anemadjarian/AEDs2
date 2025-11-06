/*
Crie um método iterativo que recebe uma string como parâmetro e retorna true se essa é um Palíndromo. 
Na saída padrão, para cada linha de entrada, escreva uma linha de saída com SIM/NÃO indicando se a linha é um palíndromo. 
Destaca-se que uma linha de entrada pode ter caracteres não letras.
*/

import java.util.*;
public class Palindromo {
    public static boolean Pali (String str){
        String str2="";
        int j=str.length()-1; // j é igual a ultima posição do array (tamanho da string - 1)
        for(int i=0; i<str.length(); i++){
            str2+=str.charAt(j);
            j--;
        } //Vai adicionando, de forma invertida, as letras da string 1 para a string 2
        int cont=0;
        for(int i=0; i<str.length(); i++){
            if(str.charAt(i)==str2.charAt(i)){
                cont++;
            }
        } //Verifica se a string normal é igual a string invertida. A cada letra igual adiciona 1 ao contador
        if(cont==str.length()){ //Se o contador for igual ao numero de letras, então todas as letras são iguais, é palindromo
            return true;
        } else{
            return false;
        }
    }
    public static void main(String[] args){
        Scanner scanf = new Scanner(System.in);
        String str = scanf.nextLine();
        while (!(str.length() == 3 && str.charAt(0) == 'F' && str.charAt(1) == 'I' && str.charAt(2) == 'M')) {
            boolean resp = Pali(str); //While verifica se a String não é a palavra FIM, se for o loop acaba
            if (resp) {
                System.out.println("SIM");
            } else {
                System.out.println("NAO");
            }
            str = scanf.nextLine(); //Scaneia até o loop acabar
        }
        scanf.close();
    }
}