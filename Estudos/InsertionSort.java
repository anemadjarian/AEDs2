import java.util.*;
public class InsertionSort {

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

    public static void insertionSort(int array[], int n){
        for(int i=1; i<n; i++){
            int tmp = array[i];
            int j=i-1;
            while((j>=0) && (array[j] > tmp)){
                array[j+1] = array[j];
                j--;
            }
            array[j+1] = tmp;
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

        insertionSort(array, n);

        mostrar(array, n);

        sc.close();
    }
}