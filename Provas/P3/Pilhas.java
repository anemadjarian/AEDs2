import java.util.*;

class Pilhas {

    int remover(int pilha[], int[] tam) {
        if (tam[0] == 0) return -1;

        int removido = pilha[0];
        for (int i = 1; i < tam[0]; i++) {
            pilha[i-1] = pilha[i];
        }
        tam[0]--;
        return removido;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Pilhas p = new Pilhas();

        int n = sc.nextInt();

        while (n != 0) {

            int[] p1 = new int[n];
            int[] p2 = new int[n];
            int[] p3 = new int[n];

            for (int i = 0; i < n; i++) {
                p1[i] = sc.nextInt();
                p2[i] = sc.nextInt();
                p3[i] = sc.nextInt();
            }

            int[] t1 = {n};
            int[] t2 = {n};
            int[] t3 = {n};

            boolean resp = false;
            boolean mudou = true;

            while (t1[0] > 0 || t2[0] > 0 || t3[0] > 0) {

                mudou = false;

                if (t1[0] > 0 && t2[0] > 0 && t3[0] > 0 &&
                    (p1[0] + p2[0] + p3[0]) % 3 == 0) {

                    p.remover(p1, t1);
                    p.remover(p2, t2);
                    p.remover(p3, t3);
                    mudou = true;
                    resp = true;
                }

                else if (t1[0] > 0 && t2[0] > 0 &&
                        (p1[0] + p2[0]) % 3 == 0) {

                    p.remover(p1, t1);
                    p.remover(p2, t2);
                    mudou = true;
                    resp = true;
                }

                else if (t2[0] > 0 && t3[0] > 0 &&
                        (p2[0] + p3[0]) % 3 == 0) {

                    p.remover(p2, t2);
                    p.remover(p3, t3);
                    mudou = true;
                    resp = true;
                }

                else if (t1[0] > 0 && t3[0] > 0 &&
                        (p1[0] + p3[0]) % 3 == 0) {

                    p.remover(p1, t1);
                    p.remover(p3, t3);
                    mudou = true;
                    resp = true;
                }

                else if (t1[0] > 0 && p1[0] % 3 == 0) {
                    p.remover(p1, t1);
                    mudou = true;
                    resp = true;
                }

                else if (t2[0] > 0 && p2[0] % 3 == 0) {
                    p.remover(p2, t2);
                    mudou = true;
                    resp = true;
                }

                else if (t3[0] > 0 && p3[0] % 3 == 0) {
                    p.remover(p3, t3);
                    mudou = true;
                    resp = true;
                }

                if (!mudou) break; // não tem mais o que remover → para
            }

            System.out.println(resp ? "1" : "0");

            n = sc.nextInt();
        }
    }
}
