import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;


class HuffmanNode {

  private char ch;
  private int frequency;
  private HuffmanNode left;
  private HuffmanNode right;

  // Getters and setters
  public char getCh() {
    return ch;
  }

  public int getFrequency() {
    return frequency;
  }

  public HuffmanNode getLeft() {
    return left;
  }

  public HuffmanNode getRight() {
    return right;
  }

  public void setCh(char ch) {
    this.ch = ch;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public void setLeft(HuffmanNode left) {
    this.left = left;
  }

  public void setRight(HuffmanNode right) {
    this.right = right;
  }
}

class MyComparator implements Comparator<HuffmanNode> {

  public int compare(HuffmanNode x, HuffmanNode y) {
    return x.getFrequency() - y.getFrequency();
  }
}

public class HuffmanCoding {

  PriorityQueue<HuffmanNode> queue;
  HashMap<Character, String> charToCode;
  HashMap<String, Character> codeToChar;

  // public static void main(String[] args) {
  // }

  public static void main(String[] args) {
    // 为了适配Mac与Windows环境下中文编码正确
    try {
      System.setOut(new PrintStream(System.out, true, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }


    // 创建 HuffmanCoding 对象
    HuffmanCoding hc = new HuffmanCoding();

    // 从文件中读取字符权重，并初始化哈夫曼�?
    hc.initializeFromFile("./letter-weight.txt");
    System.out.println("从文件中读取字符权重并初始化哈夫曼树完成");

    // 用于测试的文�?
    String test = "HELLO WORLD";

    // 编码文本
    String encodedText = hc.encode(test);
    System.out.println("原始文本: " + test);
    System.out.println("编码后的文本: " + encodedText);

    // 解码文本
    String decodedText = hc.decode(encodedText);
    System.out.println("解码后的文本: " + decodedText);

    // 计算压缩率?
    double compressionRatio = hc.calculateCompressionRatio(test, encodedText);
    System.out.println("压缩�?: " + compressionRatio);

    // 打印哈夫曼树
    System.out.println("哈夫曼树:");
    HuffmanNode root = hc.getRoot();
    hc.printTree(root, "");
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
        throw new IllegalArgumentException(
          "Character " + text.charAt(i) + " is not in the Huffman Tree."
        );
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

  public void initializeFromFile(String filePath) {
    HashMap<Character, Integer> frequency = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(":");
        if (parts.length != 2) {
          throw new IllegalArgumentException("Invalid format in input file.");
        }
        char ch;
        if (parts[0].equals("Space")) {
          ch = ' ';
        } else {
          ch = parts[0].charAt(0);
        }
        int freq = (int) Math.round(Double.parseDouble(parts[1]) * 1000); // multiply by 1000 and round to nearest integer
        frequency.put(ch, freq);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    buildTree(frequency);
    generateCodes(queue.peek(), new StringBuilder());
  }

  public double calculateCompressionRatio(
    String originalText,
    String encodedText
  ) {
    int originalDataSize = originalText.length() * 8; // each character is typically 8 bits
    int compressedDataSize = encodedText.length();
    return (double) compressedDataSize / originalDataSize;
  }

  public HuffmanNode getRoot() {
    return queue.peek();
  }

  public void printTree(HuffmanNode node, String prefix) {
    if (node != null) {
      printTree(node.getLeft(), prefix + "0");
      System.out.println(prefix + ": " + node.getCh());
      printTree(node.getRight(), prefix + "1");
    }
  }
}
