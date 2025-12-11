import java.io.*;
import java.text.*;
import java.util.*;

public class GameRehash {

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

    public GameRehash() {
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

    public static Map<Integer, GameRehash> loadCSV(String file) throws IOException {
        Map<Integer, GameRehash> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // cabeçalho
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] f = splitCSV(line);
                if (f.length < 2) continue; // requer ao menos id e name
                GameRehash g = new GameRehash();
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

    static class HashReserva {

        private GameRehash[] tabela = new GameRehash[30];
        private int tamPrincipal = 21;

        private int comparacoes = 0;

        public int getComparacoes() {
            return comparacoes;
        }

        private int asciiSum(String s) {
            int soma = 0;
            for (int i = 0; i < s.length(); i++) soma += s.charAt(i);
            return soma;
        }

        public int hash(String nome) {
            return (asciiSum(nome)+1) % tamPrincipal;
        }

        public int rehash(String nome) {
            return (asciiSum(nome)+1) % tamPrincipal;
        }

        public void inserir(GameRehash g) {
            int pos = hash(g.name);

            // tentativa na área principal
            if (tabela[pos] == null) {
                tabela[pos] = g;
            } else {
                // insere na área de reserva
                for (int i = tamPrincipal; i < 30; i++) {
                    if (tabela[i] == null) {
                        tabela[i] = g;
                        return;
                    }
                }
            }
        }

        public int buscar1(String nome) {
            int pos = hash(nome);

            // compara posição principal
            comparacoes++;
            if (tabela[pos] != null && tabela[pos].name.equals(nome)) {
                return pos;
            }

            // compara área de reserva
            for (int i = tamPrincipal; i < 30; i++) {
                comparacoes++;
                if (tabela[i] != null && tabela[i].name.equals(nome)) {
                    return i;
                }
            }

            return pos;
        }

        public boolean buscar2(String nome) {
            int pos = hash(nome);

            // compara posição principal
            comparacoes++;
            if (tabela[pos] != null && tabela[pos].name.equals(nome)) {
                return true;
            }

            // compara área de reserva
            for (int i = tamPrincipal; i < 30; i++) {
                comparacoes++;
                if (tabela[i] != null && tabela[i].name.equals(nome)) {
                    return true;
                }
            }

            return false;
        }

        public int buscar3(String nome) {
            int pos = rehash(nome);

            // compara posição principal
            comparacoes++;
            if (tabela[pos] != null && tabela[pos].name.equals(nome)) {
                return pos;
            }

            // compara área de reserva
            for (int i = tamPrincipal; i < 30; i++) {
                comparacoes++;
                if (tabela[i] != null && tabela[i].name.equals(nome)) {
                    return i;
                }
            }

            return pos;
        }
        
        public boolean buscar4(String nome) {
            int pos = rehash(nome);

            // compara posição principal
            comparacoes++;
            if (tabela[pos] != null && tabela[pos].name.equals(nome)) {
                return true;
            }

            // compara área de reserva
            for (int i = tamPrincipal; i < 30; i++) {
                comparacoes++;
                if (tabela[i] != null && tabela[i].name.equals(nome)) {
                    return true;
                }
            }

            return false;
        }
    }



    public static void main(String[] args) throws Exception {

        String csvPath = "/tmp/games.csv";

        Map<Integer, GameRehash> allGames;
        try {
            allGames = loadCSV(csvPath);
        } catch (IOException e) {
            System.err.println("Erro ao ler CSV em " + csvPath + ": " + e.getMessage());
            return;
        }

        Scanner sc = new Scanner(System.in);
        List<GameRehash> listaInsercao = new ArrayList<>();

        // Entrada até FIM (IDs)
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equals("FIM")) break;
            if (line.isEmpty()) continue;

            try {
                int id = Integer.parseInt(line);
                if (allGames.containsKey(id)) {
                    listaInsercao.add(allGames.get(id));
                }
            } catch (Exception ignore) {}
        }

        HashReserva tabela = new HashReserva();

        long tempoInicio = System.currentTimeMillis();

        for (GameRehash g : listaInsercao) {
            tabela.inserir(g);
        }

        // Consultas por nome
        while (sc.hasNextLine()) {
            String nomeBusca = sc.nextLine().trim();
            if (nomeBusca.equals("FIM")) break;
            if (nomeBusca.isEmpty()) continue;

            int posReal = tabela.buscar1(nomeBusca);
            boolean resp = tabela.buscar2(nomeBusca);

            int posReal2 = tabela.buscar3(nomeBusca);
            boolean resp2 = tabela.buscar4(nomeBusca);

            if(resp==false){
                while(posReal2 != posReal){
                    posReal2 = tabela.buscar3(nomeBusca);
                    resp2 = tabela.buscar4(nomeBusca);
                }
            }
            
            int aa= posReal-1;

            if (resp==true) {
                System.out.println(nomeBusca + ":  (Posicao: " + aa + ") SIM");
            } else {
                System.out.println(nomeBusca + ":  (Posicao: " + aa + ") NAO");
            }
        }

        long tempoExecucao = System.currentTimeMillis() - tempoInicio;

        String matricula = "890191";
        String nomeArquivoLog = matricula + "_hashReserva.txt";

        try (PrintWriter log = new PrintWriter(new FileWriter(nomeArquivoLog))) {
            log.println(matricula);
            log.println("NUMERO DE COMPARACOES: " + tabela.getComparacoes());
            log.println("TEMPO DE EXECUCAO (ms): " + tempoExecucao);
        }

        sc.close();
    }
}