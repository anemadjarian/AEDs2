/*
O Imperador Júlio César foi um dos principais nomes do Império Romano. Entre suas contribuições, temos um algoritmo de 
criptografia chamado Ciframento de César. Segundo os historiadores, César utilizava esse algoritmo para criptografar as 
mensagens que enviava aos seus generais durante as batalhas. A ideia básica é um simples deslocamento de caracteres.

Assim, por exemplo, se a chave utilizada para criptografar as mensagens for 3, todas as ocorrências do caractere 'a' 
são substituídas pelo caractere 'd', as do 'b' por 'e', e assim sucessivamente.

Crie um método iterativo que recebe uma string como parâmetro e retorna outra contendo a entrada de forma cifrada. 
Neste exercício, suponha a chave de ciframento três. Na saída padrão, para cada linha de entrada, escreva uma linha com 
a mensagem criptografada.
*/

public class CiframentoDeCesar {
    public static String codigoString(String str){
    String str2 = "";
    for(int i=0; i<str.length(); i++){
        str2 += (char) (str.charAt(i)+3); //Pula para a terceira proxima letra da tabela ASCII e força a ser char
    }
    return str2;
}
    public static void main(String[] args){
        String str = MyIO.readLine();
        while(!(str.length()==3 && str.charAt(0)=='F' && str.charAt(1)=='I' && str.charAt(2)=='M')){
            String str2 = codigoString(str); //While repete até scanear a palavra FIM
            MyIO.println(str2);
            str = MyIO.readLine(); //Scaneia a proxima palavra até o loop acabar
        }
    }
}