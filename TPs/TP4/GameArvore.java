import java.io.*;
import java.text.*;
import java.util.*;

public class GameArvore {

    public int id;
    public String name;
    public String releaseDate;
    public int estimatedOwners;

    public GameArvore() {}

    // ------------ PARSER CSV ------------------
    private static String clean(String s) {
        if (s == null) return "";
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
            s = s.substring(1, s.length() - 1);
        return s.trim();
    }

    private static int parseInt(String s, int def) {
        s = clean(s);
        try { return Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }

    private static int parseOwners(String s) {
        s = clean(s).replaceAll("[^0-9]", "");
        return s.isEmpty() ? 0 : parseInt(s, 0);
    }

    private static String parseDate(String s) {
        s = clean(s);
        if (s.isEmpty()) return "01/01/0001";
        String[] formats = {"MMM d, yyyy","MMM dd, yyyy","d MMM, yyyy","dd MMM, yyyy","MMM yyyy","yyyy"};
        for (String f : formats) {
            try {
                Date d = new SimpleDateFormat(f, Locale.ENGLISH).parse(s);
                return new SimpleDateFormat("dd/MM/yyyy").format(d);
            } catch (Exception ignore) {}
        }
        return "01/01/0001";
    }

    private static String[] splitCSV(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        int brackets = 0;
        for (char c : line.toCharArray()) {
            if (c == '"') inQuotes = !inQuotes;
            if (c == '[' && !inQuotes) brackets++;
            if (c == ']' && !inQuotes && brackets > 0) brackets--;
            if (c == ',' && !inQuotes && brackets == 0) {
                out.add(cur.toString());
                cur.setLength(0);
            } else cur.append(c);
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    public static Map<Integer, GameArvore> loadCSV(String file) throws Exception {
        Map<Integer, GameArvore> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] f = splitCSV(line);
            if (f.length < 4) continue;

            GameArvore g = new GameArvore();
            g.id = parseInt(f[0], 0);
            g.name = clean(f[1]);
            g.releaseDate = parseDate(f[2]);
            g.estimatedOwners = parseOwners(f[3]);

            if (g.id != 0) map.put(g.id, g);
        }
        br.close();
        return map;
    }

    // ================================================
    // SEGUNDA ÁRVORE — ordenada por Name
    // ================================================
    static class NoAB2 {
        String nome;
        NoAB2 esq, dir;

        NoAB2(String n) { nome = n; }
    }

    static class ArvoreNome {
        NoAB2 raiz;

        void inserir(String nome) {
            raiz = inserirRec(raiz, nome);
        }

        private NoAB2 inserirRec(NoAB2 no, String nome) {
            if (no == null) return new NoAB2(nome);
            if (nome.compareTo(no.nome) < 0) no.esq = inserirRec(no.esq, nome);
            else if (nome.compareTo(no.nome) > 0) no.dir = inserirRec(no.dir, nome);
            return no;
        }

        boolean pesquisar(String nome, StringBuilder caminho) {
            caminho.append("raiz ");
            return pesquisarRec(raiz, nome, caminho);
        }

        private boolean pesquisarRec(NoAB2 no, String nome, StringBuilder c) {
            if (no == null) return false;
            if (nome.equals(no.nome)) return true;

            if (nome.compareTo(no.nome) < 0) {
                c.append("esq ");
                return pesquisarRec(no.esq, nome, c);
            } else {
                c.append("dir ");
                return pesquisarRec(no.dir, nome, c);
            }
        }
    }

    // ================================================
    // PRIMEIRA ÁRVORE — fixa, com a chave int
    // ================================================
    static class NoAB1 {
        int chave;
        NoAB1 esq, dir;

        ArvoreNome segunda;  // ÁRVORE ASSOCIADA

        NoAB1(int c) {
            chave = c;
            segunda = new ArvoreNome();
        }
    }

    static class ArvoreMod15 {
        NoAB1 raiz;

        // insere valor fixo (do enunciado)
        void inserir(int chave) {
            raiz = inserirRec(raiz, chave);
        }

        private NoAB1 inserirRec(NoAB1 no, int chave) {
            if (no == null) return new NoAB1(chave);
            if (chave < no.chave) no.esq = inserirRec(no.esq, chave);
            else if (chave > no.chave) no.dir = inserirRec(no.dir, chave);
            return no;
        }

        NoAB1 buscarNo(int chave) {
            return buscarNoRec(raiz, chave);
        }

        private NoAB1 buscarNoRec(NoAB1 no, int chave) {
            if (no == null) return null;
            if (chave == no.chave) return no;
            if (chave < no.chave) return buscarNoRec(no.esq, chave);
            else return buscarNoRec(no.dir, chave);
        }

        // --- PESQUISA GLOBAL: mostrar AB1 + mostrar AB2 ---
        boolean pesquisar(String nome, StringBuilder caminho) {
            caminho.append("raiz ");
            return pesquisarTodos(raiz, nome, caminho);
        }

        private boolean pesquisarTodos(NoAB1 no, String nome, StringBuilder caminho) {
            if (no == null) return false;

            // Pesquisar na AB2 deste nó
            caminho.append("ESQ ");
            if (no.esq != null) {}
            if (no.segunda.pesquisar(nome, caminho)) return true;

            caminho.append("DIR ");
            if (no.dir != null) {}

            // Buscar esquerda
            if (pesquisarTodos(no.esq, nome, caminho)) return true;

            // Buscar direita
            if (pesquisarTodos(no.dir, nome, caminho)) return true;

            return false;
        }
    }

    // ================================================
    // MAIN
    // ================================================
    public static void main(String[] args) throws Exception {

        String csv = "/tmp/games.csv";
        Map<Integer, GameArvore> all = loadCSV(csv);

        Scanner sc = new Scanner(System.in);

        // --- CRIAR A PRIMEIRA ÁRVORE FIXA ---
        int[] ordem = {7,3,11,1,5,9,13,0,2,4,6,8,10,12,14};
        ArvoreMod15 AB1 = new ArvoreMod15();
        for (int x : ordem) AB1.inserir(x);

        // --- LER IDs E INSERIR NAS ÁRVORES ASSOCIADAS ---
        while (sc.hasNext()) {
            String s = sc.next();
            if (s.equals("FIM")) break;

            int id = Integer.parseInt(s);
            GameArvore g = all.get(id);

            int k = g.estimatedOwners % 15;
            NoAB1 no = AB1.buscarNo(k);
            no.segunda.inserir(g.name);
        }

        sc.nextLine();

        // --- PESQUISA ---
        while (sc.hasNextLine()) {
            String nome = sc.nextLine().trim();
            if (nome.equals("FIM")) break;

            StringBuilder caminho = new StringBuilder();
            boolean ok = AB1.pesquisar(nome, caminho);

            System.out.println("=> " + nome + " => " + caminho + (ok ? "SIM" : "NAO"));
        }
    }
}
