package com.example.tpoop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Parseur JSON minimal — zéro dépendance externe, uniquement java.*.
 *
 * Produit une structure imbriquée de :
 *   Map<String,Object>  pour les objets JSON  { }
 *   List<Object>        pour les tableaux JSON [ ]
 *   String, Double, Boolean, null pour les scalaires
 *
 * Usage :
 *   Map<String,Object> root = MiniJson.parseFile(Path.of("farm_data.json"));
 *   String nom = MiniJson.str(root, "ferme.nom");
 *   List<Object> zones = MiniJson.list(root, "ferme.zones");
 */
@SuppressWarnings("unchecked")
public class MiniJson {

    // ──────────────────────────────────────────────────────────────────
    // POINT D'ENTREE PUBLICS
    // ──────────────────────────────────────────────────────────────────

    public static Map<String, Object> parseFile(Path path) throws IOException {
        String text = Files.readString(path);
        return (Map<String, Object>) new Parser(text).parseValue();
    }

    /** Navigue un chemin "a.b.c" dans l'arbre, retourne null si absent. */
    public static Object get(Map<String, Object> root, String dotPath) {
        String[] keys = dotPath.split("\\.", 2);
        Object val = root.get(keys[0]);
        if (keys.length == 1 || val == null) return val;
        if (val instanceof Map) return get((Map<String, Object>) val, keys[1]);
        return null;
    }

    public static String str(Map<String, Object> node, String key) {
        Object v = node.get(key);
        return v == null ? null : v.toString();
    }

    public static double dbl(Map<String, Object> node, String key) {
        Object v = node.get(key);
        if (v == null) return 0.0;
        return ((Number) v).doubleValue();
    }

    public static int integer(Map<String, Object> node, String key) {
        Object v = node.get(key);
        if (v == null) return 0;
        return ((Number) v).intValue();
    }

    public static boolean bool(Map<String, Object> node, String key) {
        Object v = node.get(key);
        if (v == null) return false;
        return (Boolean) v;
    }

    public static List<Object> list(Map<String, Object> node, String key) {
        Object v = node.get(key);
        if (v == null) return Collections.emptyList();
        return (List<Object>) v;
    }

    public static Map<String, Object> obj(Map<String, Object> node, String key) {
        Object v = node.get(key);
        if (v == null) return Collections.emptyMap();
        return (Map<String, Object>) v;
    }

    // ──────────────────────────────────────────────────────────────────
    // PARSEUR INTERNE
    // ──────────────────────────────────────────────────────────────────

    private static final class Parser {
        private final String s;
        private int pos = 0;

        Parser(String s) { this.s = s; }

        Object parseValue() {
            skipWs();
            if (pos >= s.length()) throw new RuntimeException("Unexpected end of JSON");
            char c = s.charAt(pos);
            return switch (c) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't', 'f' -> parseBoolean();
                case 'n' -> parseNull();
                default  -> parseNumber();
            };
        }

        Map<String, Object> parseObject() {
            expect('{');
            Map<String, Object> map = new LinkedHashMap<>();
            skipWs();
            if (peek() == '}') { pos++; return map; }
            while (true) {
                skipWs();
                String key = parseString();
                skipWs(); expect(':');
                Object val = parseValue();
                map.put(key, val);
                skipWs();
                char sep = s.charAt(pos++);
                if (sep == '}') break;
                if (sep != ',') throw new RuntimeException("Expected ',' or '}' at " + pos);
            }
            return map;
        }

        List<Object> parseArray() {
            expect('[');
            List<Object> list = new ArrayList<>();
            skipWs();
            if (peek() == ']') { pos++; return list; }
            while (true) {
                list.add(parseValue());
                skipWs();
                char sep = s.charAt(pos++);
                if (sep == ']') break;
                if (sep != ',') throw new RuntimeException("Expected ',' or ']' at " + pos);
            }
            return list;
        }

        String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (pos < s.length()) {
                char c = s.charAt(pos++);
                if (c == '"') return sb.toString();
                if (c == '\\') {
                    char esc = s.charAt(pos++);
                    sb.append(switch (esc) {
                        case '"'  -> '"';
                        case '\\' -> '\\';
                        case '/'  -> '/';
                        case 'n'  -> '\n';
                        case 'r'  -> '\r';
                        case 't'  -> '\t';
                        default   -> esc;
                    });
                } else {
                    sb.append(c);
                }
            }
            throw new RuntimeException("Unterminated string");
        }

        Number parseNumber() {
            int start = pos;
            if (peek() == '-') pos++;
            while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.' || s.charAt(pos) == 'e' || s.charAt(pos) == 'E' || s.charAt(pos) == '+' || s.charAt(pos) == '-' && pos > start + 1))
                pos++;
            String num = s.substring(start, pos);
            return Double.parseDouble(num);
        }

        Boolean parseBoolean() {
            if (s.startsWith("true", pos))  { pos += 4; return Boolean.TRUE; }
            if (s.startsWith("false", pos)) { pos += 5; return Boolean.FALSE; }
            throw new RuntimeException("Invalid boolean at " + pos);
        }

        Object parseNull() {
            if (s.startsWith("null", pos)) { pos += 4; return null; }
            throw new RuntimeException("Invalid null at " + pos);
        }

        void skipWs() { while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++; }
        void expect(char c) { if (s.charAt(pos++) != c) throw new RuntimeException("Expected '" + c + "' at " + (pos-1)); }
        char peek() { skipWs(); return pos < s.length() ? s.charAt(pos) : 0; }
    }
}