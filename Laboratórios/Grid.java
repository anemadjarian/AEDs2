/*
Na Nlogônia, vai ser realizada a sensacional final mundial da fórmula 17. Os competidores se alinham na largada e 
disputam a corrida. Você vai ter acesso aos grids de largada e de chegada. A questão é determinar o número mínimo de 
ultrapassagens que foram efetuadas durante a competição.

Entrada
Cada caso de teste utiliza três linhas. A primeira linha de um caso de teste contém um inteiro N indicando o número de 
competidores. Cada competidor é identificado com um número de 1 a N. A segunda linha de cada caso tem os N competidores, 
em ordem do grid de largada. A terceira linha de cada caso tem os mesmos competidores, porém agora na ordem de chegada.

Saída
Para cada caso de teste imprima uma linha contendo um único número inteiro, que indica o número mínimo de ultrapassagens 
necessárias para se chegar do grid de largada ao grid de chegada.
*/

import java.util.*;
public class Grid{
	public static void preencherVetor(int nCarros, int[] carrosPosicao, Scanner sc){
		for(int i=0; i<nCarros; i++){
			carrosPosicao[i] = sc.nextInt();
		}
	}
	public static int contUltrapassagens(int[] carrosLargada, int[] carrosChegada, int j){
		int cont=0, i=0;
		while(!(carrosChegada[i]==carrosLargada[j])){
			cont++;
			i++;
		}
		return cont;
	}
	public static int ultrapassagens(int nCarros, int[] carrosLargada, int[] carrosChegada){
		int ultra=0;
		for(int j=0; j<nCarros; j++){
			if(!(carrosLargada[j]==carrosChegada[j])){
				ultra=contUltrapassagens(carrosLargada, carrosChegada, j);
			}
		}
		if(ultra==0){
			for(int j=0; j<nCarros; j++){
			if(!(carrosLargada[j]==carrosChegada[j])){
				ultra+=contUltrapassagens(carrosLargada, carrosChegada, j);
			}
		}

		}
		return ultra;
	}
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		int nCarros;
		int[] carrosLargada;
		int[] carrosChegada;
		while(sc.hasNext()){
			nCarros = sc.nextInt();
			carrosChegada = new int [nCarros];
			carrosLargada = new int [nCarros];
			preencherVetor(nCarros, carrosLargada, sc);
			preencherVetor(nCarros, carrosChegada, sc);
			int ultra = ultrapassagens(nCarros, carrosLargada, carrosChegada);
			System.out.println(ultra);
		}
	}
}
		