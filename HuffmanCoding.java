import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;

class HuffmanNode {
    private char ch;
    private int frequency;
    private HuffmanNode left;
    private HuffmanNode right;

    // Getters and setters
    public char getCh() { return ch; }
    public int getFrequency() { return frequency; }
    public HuffmanNode getLeft() { return left; }
    public HuffmanNode getRight() { return right; }

    public void setCh(char ch) { this.ch = ch; }
    public void setFrequency(int frequency) { this.frequency = frequency; }
    public void setLeft(HuffmanNode left) { this.left = left; }
    public void setRight(HuffmanNode right) { this.right = right; }
}

class MyComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y)
    {
        return x.getFrequency() - y.getFrequency();
    }
}

public class HuffmanCoding {
    PriorityQueue<HuffmanNode> queue;
    HashMap<Character, String> charToCode;
    HashMap<String, Character> codeToChar;

    public static void main(String[] args) {
        HuffmanCoding hc = new HuffmanCoding();
        String test = "hello world";
        hc.initialize(test);
        String encodedText = hc.encode(test);
        String decodedText = hc.decode(encodedText);
        System.out.println("Original Text: " + test);
        System.out.println("Encoded Text: " + encodedText);
        System.out.println("Decoded Text: " + decodedText);
    }

    public HuffmanCoding() {
        this.queue = new PriorityQueue<HuffmanNode>(new MyComparator());
        this.charToCode = new HashMap<Character, String>();
        this.codeToChar = new HashMap<String, Character>();
    }

    private HashMap<Character, Integer> calculateFrequency(String text) {
        HashMap<Character, Integer> frequency = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            if (!frequency.containsKey(text.charAt(i))) {
                frequency.put(text.charAt(i), 0);
            }
            frequency.put(text.charAt(i), frequency.get(text.charAt(i)) + 1);
        }
        return frequency;
    }

    private void buildTree(HashMap<Character, Integer> frequency) {
        for (Map.Entry<Character, Integer> entry : frequency.entrySet()) {
            HuffmanNode node = new HuffmanNode();
            node.setCh(entry.getKey());
            node.setFrequency(entry.getValue());
            queue.add(node);
        }

        while (queue.size() > 1) {
            HuffmanNode x = queue.peek();
            queue.poll();

            HuffmanNode y = queue.peek();
            queue.poll();

            HuffmanNode sum = new HuffmanNode();

            sum.setFrequency(x.getFrequency() + y.getFrequency());
            sum.setLeft(x);
            sum.setRight(y);
            queue.add(sum);
        }
    }

    private void generateCodes(HuffmanNode node, StringBuilder code) {
        if (node != null) {
            if (node.getLeft() == null && node.getRight() == null) {
                charToCode.put(node.getCh(), code.toString());
                codeToChar.put(code.toString(), node.getCh());
            } else {
                code.append('0');
                generateCodes(node.getLeft(), code);
                code.deleteCharAt(code.length() - 1);

                code.append('1');
                generateCodes(node.getRight(), code);
                code.deleteCharAt(code.length() - 1);
            }
        }
    }

    public void initialize(String text) {
        HashMap<Character, Integer> frequency = calculateFrequency(text);
        buildTree(frequency);
        generateCodes(queue.peek(), new StringBuilder());
    }

    public String encode(String text) {
        StringBuilder encodedText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String encodedChar = charToCode.get(text.charAt(i));
            if (encodedChar == null) {
                throw new IllegalArgumentException("Character " + text.charAt(i) + " is not in the Huffman Tree.");
            }
            encodedText.append(encodedChar);
        }
        return encodedText.toString();
    }

    public String decode(String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < encodedText.length(); i++) {
            temp.append(encodedText.charAt(i));
            Character ch = codeToChar.get(temp.toString());
            if (ch != null) {
                decodedText.append(ch);
                temp = new StringBuilder();
            }
        }
        if (temp.length() > 0) {
            throw new IllegalArgumentException("Invalid Huffman Code.");
        }
        return decodedText.toString();
    }

}
