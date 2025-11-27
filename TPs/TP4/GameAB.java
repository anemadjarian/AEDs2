import java.io.*;
import java.text.*;
import java.util.*;

public class GameAB {

    public int id;
    public String name;
    public String releaseDate;
    public int estimatedOwners;
    public float price;
    public List<String> supportedLanguages;
    public int metacriticScore;
    public float userScore;
    public int achievements;
    public List<String> publishers;
    public List<String> developers;
    public List<String> categories;
    public List<String> genres;
    public List<String> tags;

    public GameAB() {
        supportedLanguages = new ArrayList<>();
        publishers = new ArrayList<>();
        developers = new ArrayList<>();
        categories = new ArrayList<>();
        genres = new ArrayList<>();
        tags = new ArrayList<>();
    }

    // ----------------- helpers CSV -----------------
    private static String clean(String s) {
        if (s == null) return "";
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
            s = s.substring(1, s.length() - 1);
        return s.trim();
    }

    private static int parseInt(String s, int def) {
        s = clean(s);
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static int parseOwners(String s) {
        s = clean(s).replaceAll("[^0-9]", "");
        return s.isEmpty() ? 0 : parseInt(s, 0);
    }

    private static String parseDate(String s) {
        s = clean(s);
        if (s.isEmpty()) return "01/01/0001";
        String[] formats = {"MMM d, yyyy", "MMM dd, yyyy", "d MMM, yyyy", "dd MMM, yyyy", "MMM yyyy", "yyyy"};
        for (String f : formats) {
            try {
                Date d = new SimpleDateFormat(f, Locale.ENGLISH).parse(s);
                return new SimpleDateFormat("dd/MM/yyyy").format(d);
            } catch (ParseException ignore) {}
        }
        return "01/01/0001";
    }

    private static List<String> parseList(String s) {
        s = clean(s);
        if (s.isEmpty()) return new ArrayList<>();
        if (s.startsWith("[") && s.endsWith("]")) s = s.substring(1, s.length() - 1);
        String[] parts = s.split(",");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            String item = clean(p);
            if (!item.isEmpty() && !list.contains(item)) list.add(item);
        }
        return list;
    }

    // splitCSV robusto
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

    public static Map<Integer, GameAB> loadCSV(String file) throws IOException {
        Map<Integer, GameAB> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // cabeçalho
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] f = splitCSV(line);
                if (f.length < 2) continue; // requer ao menos id e name
                GameAB g = new GameAB();
                g.id = parseInt(f[0], 0);
                g.name = clean(f[1]);
                g.releaseDate = (f.length > 2) ? parseDate(f[2]) : "01/01/0001";
                g.estimatedOwners = (f.length > 3) ? parseOwners(f[3]) : 0;
                if (f.length > 5) g.supportedLanguages = parseList(f[5]);
                if (g.id != 0) map.put(g.id, g);
            }
        }
        return map;
    }

    // ------------------------------------------------
    // Árvore Binária
    // ------------------------------------------------
    static class No {
        GameAB game;
        No esq, dir;

        No(GameAB g) {
            this.game = g;
            this.esq = this.dir = null;
        }
    }

    static class ArvoreBinaria {
        No raiz;
        // contador global de comparações (acessível externamente)
        long comparacoes;

        ArvoreBinaria() {
            raiz = null;
            comparacoes = 0;
        }

        public void inserir(GameAB g) {
            raiz = inserirRec(raiz, g);
        }

        private No inserirRec(No no, GameAB g) {
            if (no == null) return new No(g);

            comparacoes++; // comparando nomes para decidir inserir
            int cmp = g.name.compareTo(no.game.name);

            if (cmp < 0) {
                no.esq = inserirRec(no.esq, g);
            } else if (cmp > 0) {
                no.dir = inserirRec(no.dir, g);
            } else {
                // cmp == 0 -> não insere duplicado
            }
            return no;
        }

        // Pesquisa que constrói caminho e incrementa comparações
        public boolean pesquisarComCaminho(String nome, StringBuilder caminho) {
            caminho.append("=>raiz  "); // dois espaços após raiz
            return pesquisarRec(raiz, nome, caminho);
        }

        private boolean pesquisarRec(No no, String nome, StringBuilder caminho) {
            if (no == null) return false;

            comparacoes++; // cada comparação de nomes
            int cmp = nome.compareTo(no.game.name);

            if (cmp == 0) {
                return true;
            } else if (cmp < 0) {
                caminho.append("esq ");
                return pesquisarRec(no.esq, nome, caminho);
            } else {
                caminho.append("dir ");
                return pesquisarRec(no.dir, nome, caminho);
            }
        }

        public long getComparacoes() {
            return comparacoes;
        }
    }

    // ------------------------------------------------
    // Programa principal
    // ------------------------------------------------
    public static void main(String[] args) throws Exception {
        // Ajuste o caminho do CSV conforme seu ambiente
        String csvPath = "/tmp/games.csv";

        Map<Integer, GameAB> allGames;
        try {
            allGames = loadCSV(csvPath);
        } catch (IOException e) {
            System.err.println("Erro ao ler CSV em " + csvPath + ": " + e.getMessage());
            return;
        }

        Scanner sc = new Scanner(System.in);
        List<GameAB> listaInsercao = new ArrayList<>();

        // Lê IDs até "FIM"
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line == null) break;
            line = line.trim();
            if (line.equals("FIM")) break;
            if (line.isEmpty()) continue;
            try {
                int id = Integer.parseInt(line);
                if (allGames.containsKey(id)) {
                    listaInsercao.add(allGames.get(id));
                }
            } catch (NumberFormatException ignore) {
                // ignora linhas não numéricas possivelmente entre entradas
            }
        }

        // Cria árvore e insere
        ArvoreBinaria arv = new ArvoreBinaria();
        long tempoInicio = System.currentTimeMillis();
        for (GameAB g : listaInsercao) {
            arv.inserir(g);
        }

        // Lê nomes e pesquisa, imprimindo no formato requisitado
        while (sc.hasNextLine()) {
            String nomeBusca = sc.nextLine();
            if (nomeBusca == null) break;
            nomeBusca = nomeBusca.trim();
            if (nomeBusca.equals("FIM")) break;
            if (nomeBusca.isEmpty()) continue;

            StringBuilder caminho = new StringBuilder();
            boolean achou = arv.pesquisarComCaminho(nomeBusca, caminho);

            // Imprime exatamente: NOME: <caminho>SIM/NAO
            System.out.println(nomeBusca + ": " + caminho.toString() + (achou ? "SIM" : "NAO"));
        }

        long tempoExecucao = System.currentTimeMillis() - tempoInicio;

        // Grava arquivo de log com o nome solicitado
        String matricula = "890191"; // ajuste sua matrícula se necessário
        String nomeArquivoLog = "matrícula_arvoreBinaria.txt"; // conforme enunciado

        try (PrintWriter log = new PrintWriter(new FileWriter(nomeArquivoLog))) {
            log.println(matricula);
            log.println("NUMERO DE COMPARACOES: " + arv.getComparacoes());
            log.println("TEMPO DE EXECUCAO (ms): " + tempoExecucao);
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo de log: " + e.getMessage());
        }

        sc.close();
    }
}
