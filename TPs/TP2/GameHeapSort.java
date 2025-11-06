import java.io.*;
import java.text.*;
import java.util.*;

public class GameHeapSort {

    // ------------------------------------------------------------
    // Atributos do jogo
    // ------------------------------------------------------------
    public int id;                  // ID do jogo (AppID)
    public String name;             // Nome do jogo
    public String releaseDate;      // Data de lançamento
    public int estimatedOwners;     // Número estimado de donos
    public float price;             // Preço (não usado no Heapsort)
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
    public GameHeapSort() {
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

    private static float parseFloat(String s) {
        s = clean(s).replaceAll("[^0-9\\.,]", ""); // remove caracteres não numéricos
        s = s.replace(",", "."); // caso venha com vírgula
        if (s.isEmpty()) return 0;
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return 0;
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

    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        return "[" + String.join(", ", list) + "]"; //Concatena valores
    }



    public static String formatFloat(float f) {
        if(f == (int)f) return String.format("%.1f", f); // se for inteiro, 1 casa
        return String.format("%.2f", f); // caso contrário, 2 casas
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

    public static Map<Integer, GameHeapSort> loadCSV(String file) throws IOException {
    //Mapa= id e valor(objeto GameHeapSort)
        Map<Integer, GameHeapSort> map = new HashMap<>(); //Mapa vazio
        try (BufferedReader br = new BufferedReader(new FileReader(file))) { //Buffer para as linhas
            br.readLine(); // Pula o cabeçalho
            String line;
            while ((line = br.readLine()) != null) { //Lê linha por linha
                if (line.trim().isEmpty()) continue; //Ignora linhas vazias
                String[] f = splitCSV(line); //Separa tokens
                if (f.length < 14) continue; //Se não tiver todos os atributos ignora

                GameHeapSort g = new GameHeapSort();
                g.id = parseInt(f[0], 0);
                g.name = clean(f[1]);
                g.releaseDate = parseDate(f[2]);
                g.estimatedOwners = parseOwners(f[3]);
                g.price = parseFloat(f[4]);
                g.supportedLanguages = parseList(f[5]);
                g.metacriticScore = parseInt(f[6], 0);
                g.userScore = parseFloat(f[7]);
                g.achievements = parseInt(f[8], 0);
                g.publishers = parseList(f[9]);
                g.developers = parseList(f[10]);
                g.categories = parseList(f[11]);
                g.genres = parseList(f[12]);
                g.tags = parseList(f[13]);


                if (g.id != 0) map.put(g.id, g); //Não adiciona id 0
            }
        }
        return map;
    }

    // ------------------------------------------------------------
    // Mostrar
    // ------------------------------------------------------------
    public String toJudgeLine() {
        return "=> " + id + " ## " + name + " ## " + releaseDate + " ## " + estimatedOwners + " ## "
                + formatFloat(price) + " ## " + listToString(supportedLanguages) + " ## " + metacriticScore + " ## "
                + formatFloat(userScore) + " ## " + achievements + " ## " + listToString(publishers) + " ## "
                + listToString(developers) + " ## " + listToString(categories) + " ## "
                + listToString(genres) + " ## " + listToString(tags) + " ##";
    }

    // ------------------------------------------------------------
    // Variáveis globais do Heapsort
    // ------------------------------------------------------------
    static long numComparacoes = 0;
    static long numMovimentacoes = 0;

    // ------------------------------------------------------------
    // Troca elementos do array
    // ------------------------------------------------------------
    static void swap(GameHeapSort[] array, int i, int j) {
        GameHeapSort temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        numMovimentacoes += 3;
    }

    // ------------------------------------------------------------
    // Mantém a propriedade do heap
    // ------------------------------------------------------------
    static void heapify(GameHeapSort[] array, int n, int i) {
        int largest = i;
        int left = 2*i + 1;
        int right = 2*i + 2;

        if (left < n) {
            numComparacoes++;
            if (array[left].estimatedOwners > array[largest].estimatedOwners ||
               (array[left].estimatedOwners == array[largest].estimatedOwners && array[left].id > array[largest].id)) {
                largest = left;
            }
        }

        if (right < n) {
            numComparacoes++;
            if (array[right].estimatedOwners > array[largest].estimatedOwners ||
               (array[right].estimatedOwners == array[largest].estimatedOwners && array[right].id > array[largest].id)) {
                largest = right;
            }
        }

        if (largest != i) {
            swap(array, i, largest);
            heapify(array, n, largest);
        }
    }

    // ------------------------------------------------------------
    // Heapsort completo
    // ------------------------------------------------------------
    static void heapSort(GameHeapSort[] array) {
        int n = array.length;

        // Constroi heap
        for (int i = n/2 -1; i >=0; i--) heapify(array, n, i);

        // Remove elementos do heap
        for (int i = n-1; i>=0; i--) {
            swap(array, 0, i);
            heapify(array, i, 0);
        }
    }

    // ------------------------------------------------------------
    // Main
    // ------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        Map<Integer, GameHeapSort> allGames = loadCSV("/tmp/games.csv");
        Scanner sc = new Scanner(System.in);

        // Lê IDs até "FIM"
        List<GameHeapSort> lista = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equals("FIM")) break;
            int id = Integer.parseInt(line);
            if (allGames.containsKey(id)) lista.add(allGames.get(id));
        }
        sc.close();

        // Converte lista para array
        GameHeapSort[] array = lista.toArray(new GameHeapSort[0]);

        // Mede tempo
        long start = System.currentTimeMillis();
        heapSort(array);
        long end = System.currentTimeMillis();

        // Imprime resultado
        for (GameHeapSort g : array) {
            System.out.println(g.toJudgeLine());
        }

        // --------------------------------------------------------
        // Gera log
        // --------------------------------------------------------
        try (PrintWriter pw = new PrintWriter("890191_heapsort.txt")) {
            pw.println("890191\t" + numComparacoes + "\t" + numMovimentacoes + "\t" + (end-start) + "ms");
        }
    }
}