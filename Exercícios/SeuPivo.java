import java.util.*;
import java.util.Random;
public class SeuPivo {

    public static void preencheRandom(int[] array, int tamanho){
        Random vet = new Random();
        for(int i=0; i<tamanho; i++){
            array[i] = vet.nextInt(100);
        }
    }

    public static void preencheVoce(int[] array, int tamanho, Scanner sc){
        for(int i=0; i<tamanho; i++){
            array[i] = sc.nextInt();
        }
    }

    public static void swap(int[] array, int i, int j){
        int aux = array[i];
        array[i] = array[j];
        array[j] = aux;
    }

    public static void QuickSortFirstPivot(int[] array, int left, int right){
        int i=left;
        int j=right;
        int pivo=left;
        while(i<=j){
            while(array[i]<array[pivo]){
                i++;
            }
            while(array[j]>array[pivo]){
                j--;
            }
            if(i<=j){
                swap(array, i, j);
                i++;
                j--;
            }
        }
        if(left < j){
            QuickSortFirstPivot(array, left, j);
        }
        if(i < right){
            QuickSortFirstPivot(array, i, right);
        }
    }

    public static void QuickSortLastPivot(int[] array, int left, int right){
        int i=left;
        int j=right;
        int pivo=right;
        while(i<=j){
            while(array[i]<array[pivo]){
                i++;
            }
            while(array[j]>array[pivo]){
                j--;
            }
            if(i<=j){
                swap(array, i, j);
                i++;
                j--;
            }
        }
        if(left < j){
            QuickSortLastPivot(array, left, j);
        }
        if(i < right){
            QuickSortLastPivot(array, i, right);
        }
    }

    public static void QuickSortRandomPivot(int[] array, int left, int right){
        int i=left;
        int j=right;
        Random gerador = new Random();
        int pivo = left + gerador.nextInt(right-left+1);
        while(i<=j){
            while(array[i]<array[pivo]){
                i++;
            }
            while(array[j]>array[pivo]){
                j--;
            }
            if(i<=j){
                swap(array, i, j);
                i++;
                j--;
            }
        }
        if(left < j){
            QuickSortRandomPivot(array, left, j);
        }
        if(i < right){
            QuickSortRandomPivot(array, i, right);
        }
    }

    public static void QuickSortMediaOfThree(int[] array, int left, int right){
        int i=left;
        int j=right;
        int pivo=(left+right)/2;
        while(i<=j){
            while(array[i]<array[pivo]){
                i++;
            }
            while(array[j]>array[pivo]){
                j--;
            }
            if(i<=j){
                swap(array, i, j);
                i++;
                j--;
            }
        }
        if(left < j){
            QuickSortMediaOfThree(array, left, j);
        }
        if(i < right){
            QuickSortMediaOfThree(array, i, right);
        }
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int tamanho = sc.nextInt();
        int[] array;
        array = new int[tamanho];

        int left = 0;
        int right = tamanho-1;

        //Caso você queira um vetor de numeros aleatorios
        //preencheRandom(array, tamanho);

        //Caso você queira preencher o vetor manulamente
        preencheVoce(array, tamanho, sc);

        long start = System.currentTimeMillis();

        //Você precisa descomentar qual você quer usar
        QuickSortFirstPivot(array, left, right);
        //QuickSortLastPivot(array, left, right);
        //QuickSortRandomPivot(array, left, right);
        //QuickSortMediaOfThree(array, left, right);

        System.out.println("Array ordenado:");
        for(int i=0; i<tamanho; i++){
            System.out.print(array[i] + " ");
        }
        System.out.println(" ");

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Tempo de execução: " + elapsed);

    }
}
