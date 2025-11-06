import java.io.*;
import java.text.*;
import java.util.*;

public class GameMergeSort {

    // ------------------------------------------------------------
    // Atributos do jogo
    // ------------------------------------------------------------
    public int id;                  // ID do jogo (AppID)
    public String name;             // Nome do jogo
    public String releaseDate;      // Data de lançamento
    public int estimatedOwners;     // Número estimado de donos
    public float price;             // Preço
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
    public GameMergeSort() {
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

    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        return "[" + String.join(", ", list) + "]"; //COncatena valores
    }


    public static String formatFloat(float f) {
    // Se for exatamente 0, imprime 0.0
    if(f == 0.0f) return "0.0";

    // Se for número inteiro, imprime com .0
    if(f == (long) f) return String.format("%d.0", (long) f);

    // Para demais valores, mantém até 2 casas decimais e remove 0 final desnecessário
    return String.format("%.2f", f).replaceAll("0$", "");
}


    private static float parseFloat(String s) {
        if(s.equals("0.0")) return 0.0f;
        s = clean(s).replaceAll("[^0-9\\.,]", ""); // remove caracteres não numéricos
        s = s.replace(",", "."); // vírgula vira ponto
        if (s.isEmpty()) return 0;
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return 0;
        }
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

    public static Map<Integer, GameMergeSort> loadCSV(String file) throws IOException {
    //Mapa= id e valor(objeto GameMergeSort)
        Map<Integer, GameMergeSort> map = new HashMap<>(); //Mapa vazio
        try (BufferedReader br = new BufferedReader(new FileReader(file))) { //Buffer para as linhas
            br.readLine(); // Pula o cabeçalho
            String line;
            while ((line = br.readLine()) != null) { //Lê linha por linha
                if (line.trim().isEmpty()) continue; //Ignora linhas vazias
                String[] f = splitCSV(line); //Separa tokens
                if (f.length < 14) continue; //Se não tiver todos os atributos ignora

                GameMergeSort g = new GameMergeSort();
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
    // Contadores do mergesort
    // ------------------------------------------------------------
    static long numComparacoes = 0;
    static long numMovimentacoes = 0;

    // ------------------------------------------------------------
    // Mergesort
    // ------------------------------------------------------------
    static void merge(GameMergeSort[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        GameMergeSort[] L = new GameMergeSort[n1];
        GameMergeSort[] R = new GameMergeSort[n2];

        for (int i=0; i<n1; i++) { L[i] = arr[left+i]; numMovimentacoes++; }
        for (int i=0; i<n2; i++) { R[i] = arr[mid+1+i]; numMovimentacoes++; }

        int i=0, j=0, k=left;
        while(i<n1 && j<n2) {
            numComparacoes++;
            if (L[i].price < R[j].price || (L[i].price == R[j].price && L[i].id < R[j].id)) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
            numMovimentacoes++;
        }

        while(i<n1) { arr[k++] = L[i++]; numMovimentacoes++; }
        while(j<n2) { arr[k++] = R[j++]; numMovimentacoes++; }
    }

    static void mergeSort(GameMergeSort[] arr, int left, int right) {
        if(left < right) {
            int mid = (left+right)/2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid+1, right);
            merge(arr, left, mid, right);
        }
    }

    // ------------------------------------------------------------
    // Main
    // ------------------------------------------------------------
    public static void main(String[] args) throws Exception {
        Map<Integer, GameMergeSort> allGames = loadCSV("/tmp/games.csv");
        Scanner sc = new Scanner(System.in);

        List<GameMergeSort> lista = new ArrayList<>();
        while(sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if(line.equals("FIM")) break;
            int id = Integer.parseInt(line);
            if(allGames.containsKey(id)) lista.add(allGames.get(id));
        }
        sc.close();

        GameMergeSort[] array = lista.toArray(new GameMergeSort[0]);

        long start = System.currentTimeMillis();
        mergeSort(array, 0, array.length-1);
        long end = System.currentTimeMillis();

        // Imprime os 5 mais caros
        MyIO.println("| 5 preços mais caros |");
        for (int i = array.length - 1; i >= Math.max(array.length - 5, 0); i--) {
                System.out.println(array[i].toJudgeLine());
        }
        System.out.println(); 

        // Imprime os 5 mais baratos
        MyIO.println("| 5 preços mais baratos |");
        for(int i=0; i<Math.min(5, array.length); i++) {
            System.out.println(array[i].toJudgeLine());
        }

        // Gera arquivo de log
        try(PrintWriter pw = new PrintWriter("890191_mergesort.txt")) {
            pw.println("890191\t" + numComparacoes + "\t" + numMovimentacoes + "\t" + (end-start) + "ms");
        }
    }
}