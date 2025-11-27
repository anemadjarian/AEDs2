import java.io.*;
import java.util.*;

public class GameAV {

    // =====================================================================
    //                               GAME
    // =====================================================================
    static class Game {
        int id;
        String name;

        Game(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    // =====================================================================
    //                             NÓ AVL
    // =====================================================================
    static class No {
        Game game;
        No esq, dir;
        int altura;

        No(Game g) {
            this.game = g;
            this.esq = this.dir = null;
            this.altura = 1;
        }
    }

    // =====================================================================
    //                             VARIÁVEIS
    // =====================================================================
    static long comparacoes = 0;
    static No raiz = null;
    static HashMap<Integer, Game> jogos = new HashMap<>();

    // =====================================================================
    //                     FUNÇÕES AUXILIARES DA AVL
    // =====================================================================

    static int altura(No n) {
        return (n == null ? 0 : n.altura);
    }

    static int max(int a, int b) {
        return (a > b ? a : b);
    }

    static int fator(No n) {
        return (n == null ? 0 : altura(n.esq) - altura(n.dir));
    }

    static No rotacaoDir(No y) {
        No x = y.esq;
        No T2 = x.dir;

        x.dir = y;
        y.esq = T2;

        y.altura = max(altura(y.esq), altura(y.dir)) + 1;
        x.altura = max(altura(x.esq), altura(x.dir)) + 1;

        return x;
    }

    static No rotacaoEsq(No x) {
        No y = x.dir;
        No T2 = y.esq;

        y.esq = x;
        x.dir = T2;

        x.altura = max(altura(x.esq), altura(x.dir)) + 1;
        y.altura = max(altura(y.esq), altura(y.dir)) + 1;

        return y;
    }

    // =====================================================================
    //                          INSERÇÃO AVL
    // =====================================================================

    static No inserir(No no, Game g) {
        if (no == null) return new No(g);

        comparacoes++;
        int cmp = g.name.compareTo(no.game.name);

        if (cmp < 0)
            no.esq = inserir(no.esq, g);
        else if (cmp > 0)
            no.dir = inserir(no.dir, g);
        else
            return no; // já existe

        no.altura = 1 + max(altura(no.esq), altura(no.dir));

        int fb = fator(no);

        // Caso Esquerda-Esquerda
        if (fb > 1 && g.name.compareTo(no.esq.game.name) < 0)
            return rotacaoDir(no);

        // Caso Direita-Direita
        if (fb < -1 && g.name.compareTo(no.dir.game.name) > 0)
            return rotacaoEsq(no);

        // Caso Esquerda-Direita
        if (fb > 1 && g.name.compareTo(no.esq.game.name) > 0) {
            no.esq = rotacaoEsq(no.esq);
            return rotacaoDir(no);
        }

        // Caso Direita-Esquerda
        if (fb < -1 && g.name.compareTo(no.dir.game.name) < 0) {
            no.dir = rotacaoDir(no.dir);
            return rotacaoEsq(no);
        }

        return no;
    }

    // =====================================================================
    //                         PESQUISA COM CAMINHO
    // =====================================================================

    static boolean pesquisar(No no, String nome, StringBuilder cam) {
        if (no == null) return false;

        comparacoes++;
        int cmp = nome.compareTo(no.game.name);

        if (cmp == 0) return true;

        if (cmp < 0) {
            cam.append("esq ");
            return pesquisar(no.esq, nome, cam);
        } else {
            cam.append("dir ");
            return pesquisar(no.dir, nome, cam);
        }
    }

    // =====================================================================
    //                   LEITURA DO CSV E ARMAZENAMENTO
    // =====================================================================

    static void carregarCSV() throws Exception {
        File csv = new File("/tmp/games.csv");
        if (!csv.exists()) {
            System.out.println("Arquivo /tmp/games.csv não encontrado.");
            return;
        }

        BufferedReader br = new BufferedReader(new FileReader(csv));
        String linha = br.readLine(); // ignorar header

        while ((linha = br.readLine()) != null) {
            String[] partes = linha.split(",", 3);

            int id = Integer.parseInt(partes[0]);
            String nome = partes[1];

            jogos.put(id, new Game(id, nome));
        }

        br.close();
    }

    // =====================================================================
    //                                 MAIN
    // =====================================================================

    public static void main(String[] args) throws Exception {

        carregarCSV();
        Scanner sc = new Scanner(System.in);

        long inicio = System.currentTimeMillis();

        // ------------------------------
        //     INSERÇÃO DE ELEMENTOS
        // ------------------------------
        while (true) {
            String s = sc.nextLine();

            if (s.equals("FIM")) break;

            int id = Integer.parseInt(s);

            if (jogos.containsKey(id))
                raiz = inserir(raiz, jogos.get(id));
        }

        // ------------------------------
        //         PESQUISAS
        // ------------------------------
        while (true) {
            String nome = sc.nextLine();

            if (nome.equals("FIM")) break;

            StringBuilder cam = new StringBuilder("=>raiz ");

            boolean ok = pesquisar(raiz, nome, cam);

            System.out.println(nome + ": " + cam.toString() + (ok ? "SIM" : "NAO"));
        }

        long fim = System.currentTimeMillis();
        long tempo = fim - inicio;

        // ------------------------------
        //             LOG
        // ------------------------------
        FileWriter fw = new FileWriter("890191_arvoreAVL.txt");
        fw.write("890191\n");
        fw.write("Comparacoes: " + comparacoes + "\n");
        fw.write("Tempo(ms): " + tempo + "\n");
        fw.close();

        sc.close();
    }
}
