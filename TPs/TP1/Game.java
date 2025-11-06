import java.io.*;
import java.text.*;
import java.util.*;


public class Game {
    private int id;
    private String name;
    private String releaseDate;
    private int estimatedOwners;
    private float price;
    private List<String> supportedLanguages;
    private int metacriticScore;
    private float userScore;
    private int achievements;
    private List<String> publishers;
    private List<String> developers;
    private List<String> categories;
    private List<String> genres;
    private List<String> tags;


    public Game() {
        supportedLanguages = new ArrayList<>();
        publishers = new ArrayList<>();
        developers = new ArrayList<>();
        categories = new ArrayList<>();
        genres = new ArrayList<>();
        tags = new ArrayList<>();
    }


    //Funções
    private static String clean(String s) {
        if (s == null) return "";
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
            s = s.substring(1, s.length() - 1);
        return s.trim();
    }


    private static int parseInt(String s, int def) {
        s = clean(s);
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }


    private static float parseFloat(String s, float def) {
        s = clean(s);
        try { return Float.parseFloat(s); } catch (Exception e) { return def; }
    }


    private static int parseOwners(String s) {
        s = clean(s).replaceAll("[^0-9]", "");
        return s.isEmpty() ? 0 : parseInt(s, 0);
    }


    private static float parsePrice(String s) {
        s = clean(s);
        if (s.equalsIgnoreCase("Free to Play") || s.isEmpty()) return 0f;
        s = s.replace("$", "").replace(",", "");
        return parseFloat(s, 0f);
    }


    private static float parseUserScore(String s) {
        s = clean(s);
        if (s.equalsIgnoreCase("tbd") || s.isEmpty()) return -1f;
        return parseFloat(s, -1f);
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


    private static String listToString(List<String> list) {
        return "[" + String.join(", ", list) + "]";
    }


    private static String formatFloat(float f) {
        if (f == (int) f) return String.valueOf((int) f);
        return String.valueOf(f);
    }


    //Print
    public String toJudgeLine() {
        return "=> " + id + " ## " + name + " ## " + releaseDate + " ## " + estimatedOwners + " ## "
                + formatFloat(price) + " ## " + listToString(supportedLanguages) + " ## " + metacriticScore + " ## "
                + formatFloat(userScore) + " ## " + achievements + " ## " + listToString(publishers) + " ## "
                + listToString(developers) + " ## " + listToString(categories) + " ## "
                + listToString(genres) + " ## " + listToString(tags) + " ##";
    }


    //Leitura CSV
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
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }


    //Armazena jogos
    public static Map<Integer, Game> loadCSV(String file) throws IOException {
        Map<Integer, Game> map = new HashMap<>();


        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] f = splitCSV(line);
                if (f.length < 14) continue;


                Game g = new Game();
                g.id = parseInt(f[0], 0);
                g.name = clean(f[1]);
                g.releaseDate = parseDate(f[2]);
                g.estimatedOwners = parseOwners(f[3]);
                g.price = parsePrice(f[4]);
                g.supportedLanguages = parseList(f[5]);
                g.metacriticScore = parseInt(f[6], -1);
                g.userScore = parseUserScore(f[7]);
                g.achievements = parseInt(f[8], 0);
                g.publishers = parseList(f[9]);
                g.developers = parseList(f[10]);
                g.categories = parseList(f[11]);
                g.genres = parseList(f[12]);
                g.tags = parseList(f[13]);


                if (g.id != 0) map.put(g.id, g);
            }
        }
        return map;
    }


    //Main
    public static void main(String[] args) {
        Map<Integer, Game> games;
        try {
            games = loadCSV("/tmp/games.csv");
        } catch (IOException e) {
            System.err.println("Erro ao abrir o arquivo: " + e.getMessage());
            return;
        }


        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equals("FIM")) break;
            try {
                int id = Integer.parseInt(line);
                Game g = games.get(id);
                if (g != null) System.out.println(g.toJudgeLine());
            } catch (NumberFormatException ignored) {}
        }
        sc.close();
    }
}