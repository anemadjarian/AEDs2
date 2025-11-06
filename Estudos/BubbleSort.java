import java.util.*;
public class BubbleSort {

    public static void preencher(int array[], int n, Scanner sc){
        for(int i=0; i<n; i++){
            array[i] = sc.nextInt();
        }
    }

    public static void swap (int array[], int j){
        int aux = array[j];
        array[j] = array[j+1];
        array[j+1] = aux;
    }

    public static void bubbleSort(int array[], int n){
        for(int i=n-1; i>0; i--){
            for(int j=0; j<i; j++){
                if(array[j]>array[j+1]){
                    swap(array, j);
                }
            }
        }
    }

    public static void mostrar(int array[], int n){
        System.out.print("[ ");
        for(int i=0; i<n; i++){
            System.out.print(array[i] + " ");
        }
        System.out.println("]");
    }

    public static void main(String[] args){
        Scanner sc = new Scanner (System.in);

        int n = sc.nextInt();
        int[] array = new int[n];

        preencher(array, n, sc);

        bubbleSort(array, n);

        mostrar(array, n);

        sc.close();
    }
}