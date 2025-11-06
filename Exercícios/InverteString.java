/*
Crie um método iterativo que recebe uma string como parâmetro e retorna a string invertida. 
Na saída padrão, para cada linha de entrada, escreva uma linha de saída com a string invertida. 
Por exemplo, se a entrada for abcde, a saída deve ser edcba.
 */

import java.util.*;
public class InverteString{
    public static String inversao(String str){
        String str2= "";
        for(int i=(str.length()-1); i>=0; i--){
            str2+=str.charAt(i);
        }
        return str2;
    }
    public static void main(String[] args){
        Scanner scanf = new Scanner(System.in);
	String str = scanf.nextLine();
	while(!(str.length()==3 && str.charAt(0)=='F' && str.charAt(1)=='I' && str.charAt(2)=='M')){
        	String str2 = inversao(str);
        	System.out.println(str2);
		str = scanf.nextLine();
	}
    }
}