import java.io.*;
import java.text.*;
import java.util.*;

public class GamePesqBin {

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

    // ------------------------------------------------------------
    // Construtor
    // ------------------------------------------------------------
    public GamePesqBin() {
        supportedLanguages = new ArrayList<>();
        publishers = new ArrayList<>();
        developers = new ArrayList<>();
        categories = new ArrayList<>();
        genres = new ArrayList<>();
        tags = new ArrayList<>();
    }

    // ------------------------------------------------------------
    // Funções auxiliares
    // ------------------------------------------------------------
    private static String clean(String s) {
        if (s == null) return "";
        s = s.trim(); //Remove espaços em branco no começo e n fim da string
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
            s = s.substring(1, s.length() - 1); //Remove aspas
        return s.trim();
    }

    private static int parseInt(String s, int def) {
    s = clean(s); //Limpa a string (remove espaços e aspas)
    try { 
        return Integer.parseInt(s); //Tenta converter para inteiro
    } catch (Exception e) { 
        return def; //Se der erro (string vazia ou não numérica), retorna valor padrão
    }
}

    private static int parseOwners(String s) {
        s = clean(s).replaceAll("[^0-9]", ""); //Remove tudo não numérico
        return s.isEmpty() ? 0 : parseInt(s, 0); //Se ficar vazia, retorna 0, se não retorna inteiro
    }

    private static String parseDate(String s) {
        s = clean(s); //Limpa String
        if (s.isEmpty()) return "01/01/0001"; //Se tiver vazio retorna data padrão
        String[] formats = {"MMM d, yyyy", "MMM dd, yyyy", "d MMM, yyyy", "dd MMM, yyyy", "MMM yyyy", "yyyy"};
        for (String f : formats) {
            try {
                Date d = new SimpleDateFormat(f, Locale.ENGLISH).parse(s); //Formata datas em ingles automatico
                return new SimpleDateFormat("dd/MM/yyyy").format(d);
            } catch (ParseException ignore) {}
        }
        return "01/01/0001";
    }

    private static List<String> parseList(String s) {
        s = clean(s); 
        if (s.isEmpty()) return new ArrayList<>(); //Vazio retorna lista vazia
        if (s.startsWith("[") && s.endsWith("]")) s = s.substring(1, s.length() - 1); //Remove colchetes
        String[] parts = s.split(","); //Divide a strings por virgula
        List<String> list = new ArrayList<>(); //Cria a lista final
        for (String p : parts) {
            String item = clean(p);
            if (!item.isEmpty() && !list.contains(item)) list.add(item); //Verifica se não é vazia e não está duplicado e adiciona na lista final
        }
        return list;
    }

    // ------------------------------------------------------------
    // Leitura CSV
    // ------------------------------------------------------------
    private static String[] splitCSV(String line) {
        List<String> out = new ArrayList<>(); //Lista final
        StringBuilder cur = new StringBuilder(); //Token atual
        boolean inQuotes = false; //Verifica se tem aspas, se tiver não separa por virgula
        int brackets = 0; //Verifica sem tem colchetes
        for (char c : line.toCharArray()) { //Percorre cada caracter da linha
            if (c == '"') inQuotes = !inQuotes; //Se encontrar aspas, muda status do inQuotes
            if (c == '[' && !inQuotes) brackets++; //Conta colchetes abertos
            if (c == ']' && !inQuotes && brackets > 0) brackets--; //Fecha colchetes
            if (c == ',' && !inQuotes && brackets == 0) { //Virgula fora de aspas e fora de colchetes separa token
                out.add(cur.toString()); //Adiciona token na lista
                cur.setLength(0); //Limpa token
            } else cur.append(c); //Se não, adiciona caracter ao token
        }
        out.add(cur.toString()); //Adiciona ultimo token que não termina com virgula
        return out.toArray(new String[0]); //Lista se torna um array
    }

    public static Map<Integer, GamePesqBin> loadCSV(String file) throws IOException {
    //Mapa= id e valor(objeto GamePesqBin)
        Map<Integer, GamePesqBin> map = new HashMap<>(); //Mapa vazio
        try (BufferedReader br = new BufferedReader(new FileReader(file))) { //Buffer para as linhas
            br.readLine(); // Pula o cabeçalho
            String line;
            while ((line = br.readLine()) != null) { //Lê linha por linha
                if (line.trim().isEmpty()) continue; //Ignora linhas vazias
                String[] f = splitCSV(line); //Separa tokens
                if (f.length < 14) continue; //Se não tiver todos os atributos ignora

                GamePesqBin g = new GamePesqBin();
                g.id = parseInt(f[0], 0);
                g.name = clean(f[1]);
                g.releaseDate = parseDate(f[2]);
                g.estimatedOwners = parseOwners(f[3]);
                g.price = 0;
                g.supportedLanguages = parseList(f[5]);

                if (g.id != 0) map.put(g.id, g); //Não adiciona id 0
            }
        }
        return map;
    }

    // ------------------------------------------------------------
    // Métodos auxiliares de ordenação e busca
    // ------------------------------------------------------------
    static long numComparacoes = 0; // variável global para contagem

    static void swap(GamePesqBin[] vetor, int i, int j) {
        GamePesqBin temp = vetor[i];
        vetor[i] = vetor[j];
        vetor[j] = temp;
    }

    static void selectionSort(GamePesqBin[] vetor) {
        int n = vetor.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (vetor[j].name.compareTo(vetor[minIdx].name) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                swap(vetor, i, minIdx);
            }
        }
    }

    static boolean binarySearch(GamePesqBin[] vetor, String nome) {
        int esq = 0, dir = vetor.length - 1;
        while (esq <= dir) {
            numComparacoes++; // conta cada comparação
            int meio = (esq + dir) / 2;
            int cmp = nome.compareTo(vetor[meio].name);
            if (cmp == 0) return true;
            else if (cmp < 0) dir = meio - 1;
            else esq = meio + 1;
        }
        return false;
    }

    // ------------------------------------------------------------
    // Impressão formatada
    // ------------------------------------------------------------
    public String toJudgeLine() {
        return id + " ## " + name + " ## " + releaseDate + " ##";
    }

    // ------------------------------------------------------------
    // Main
    // ------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        Map<Integer, GamePesqBin> allGames = loadCSV("/tmp/games.csv");
        Scanner sc = new Scanner(System.in);

        //Lê IDs até "FIM"
        List<GamePesqBin> lista = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equals("FIM")) break;
            int id = Integer.parseInt(line);
            if (allGames.containsKey(id)) {
                lista.add(allGames.get(id));
            }
        }

        //Transforma em vetor e ordena pelo nome
        GamePesqBin[] vetor = lista.toArray(new GamePesqBin[0]);
        selectionSort(vetor);

        long tempoInicio = System.currentTimeMillis();
        //Lê nomes e faz pesquisa binária
        while (sc.hasNextLine()) {
            String nome = sc.nextLine().trim();
            if (nome.equals("FIM")) break;

            boolean encontrado = binarySearch(vetor, nome);
            System.out.println(encontrado ? " SIM" : " NAO");
        }

        //Arquivo LOG
        String matricula = "890191"; // sua matrícula
        long tempoExecucao = System.currentTimeMillis() - tempoInicio; // tempo em ms

        try (PrintWriter log = new PrintWriter(new FileWriter("matricula_binario.txt"))) {
            log.println(matricula);
            log.println("NUMERO DE COMPARACOES: " + numComparacoes);
            log.println("TEMPO DE EXECUCAO (ms): " + tempoExecucao);
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo de log: " + e.getMessage());
        }   

        sc.close();
    }
}