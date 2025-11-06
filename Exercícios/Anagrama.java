/*
Crie um método iterativo que recebe duas strings como parâmetros e retorna true se as strings são anagramas uma da outra, 
ou false caso contrário.

Na saída padrão, para cada par de strings de entrada, escreva uma linha de saída com SIM/NÃO indicando se as strings são 
anagramas. Por exemplo, se as entradas forem listen e silent, a saída deve ser SIM.
*/

import java.util.*;
public class Anagrama {
    public static boolean isAnagrama(String str){
        String palavraUm="";
        String palavraDois="";
        int index=-1; //Seta como -1 pq não tiver "-" começa na posição 0, já q o comando do for é index+1
        for(int i=0; i<str.length(); i++){
            if(str.charAt(i)=='-'){
                index = i;
                i=str.length(); //Pega a posição do "-" para poder separar as duas palavras
            } else{
                char letra;
                if (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z') {
                    letra = (char)(str.charAt(i)+ 32);
                    palavraUm += letra; //Transforma as letras maiusculas em minusculas e concatena na palavra um
                } else{
                    palavraUm += str.charAt(i); //Se não for letra maisucula, só concatena a letra
                }
            }
        }
        for(int i=index+1; i<str.length(); i++){
            char letra;
                if (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z') {
                    letra = (char)(str.charAt(i)+ 32);
                    palavraDois += letra;
                } else{
                    palavraDois += str.charAt(i);
                } 
        }

        if(!(palavraUm.length() == palavraDois.length())) return false; //Se as palavras tem tamanhos diferentes não sao anagramas

        int cont=0;
        for(int i=0; i<palavraUm.length(); i++){
            int j=0;
            for(j=0; j<palavraDois.length(); j++){ //Confere se  aletra i da palavra um é igual a alguma letra da palavra dois
                if(palavraUm.charAt(i)==palavraDois.charAt(j)){
                    cont++;
                    j=palavraDois.length();
                }
            }
        }
        if(cont == palavraUm.length()) return true;
        else return false;
    }
    public static void main(String args[]){
        String str = MyIO.readLine();
            while(!(str.length()==3 && str.charAt(0)=='F' && str.charAt(1)=='I' && str.charAt(2)=='M')){
                boolean resp = isAnagrama(str);
                if(resp == true) MyIO.println("SIM");
                else MyIO.println("NÃO");
                str = MyIO.readLine();
        }
    }
}