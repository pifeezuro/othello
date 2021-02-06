package othello;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int CAN_PLACE = 3;
    public static final int BOARD_SIZE = 8;

    public static void main(String[] args) {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int[] row : board) {
            Arrays.fill(row, EMPTY);
        }
        board[3][3] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        board[4][4] = WHITE;

        boolean passed = false;
        int turn = BLACK;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!hasSquareToPlace(board, turn)) {
                if (passed) {
                    break;
                }
                passed = true;
                turn = changeTurn(turn);
                continue;
            }
            draw(board);
            System.out.println("\n現在の石数: " + count(board)+ "\n");
            if (passed) {
                System.out.println((turn == BLACK ? "o" : "x") + "の番は置ける場所がないためパスされました");
            }
            passed = false;
            System.out.println((turn == BLACK ? "x" : "o") + "の番です。列と行を指定してください。(例:a1)\n");
            while (true) {
                String hand = scanner.nextLine();
                if (hand.length() != 2 || !hand.matches("^[a-h][1-8]$")) {
                    System.out.println("a1-h8の範囲で入力してください");
                    continue;
                }
                char[] handChars = hand.toCharArray();
                if (!canPlace(board, handChars[1] - '1', handChars[0] - 'a', turn, true)) {
                    System.out.println("その場所には置けません");
                    continue;
                }
                break;
            }
            turn = changeTurn(turn);
        }
        System.out.println("試合終了\n");
        draw(board);
        System.out.println("\n試合結果: " + count(board));
    }

    private static String count(int[][] board) {
        int blackStone = 0;
        int whiteStone = 0;
        for (int[] row : board) {
            for (int square : row) {
                if(square == BLACK){
                    blackStone++;
                }
                if (square == WHITE){
                    whiteStone++;
                }
            }
        }
        return "x " + blackStone + " - " + whiteStone + " o";
    }

    /**
     * 盤面における場所があるか探索.
     *
     * @param board 盤面
     * @param turn  手番
     * @return あればtrue
     */
    private static boolean hasSquareToPlace(int[][] board, int turn) {
        boolean hasSquareToPlace = false;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                if (board[row][column] == CAN_PLACE) {
                    board[row][column] = EMPTY;
                }
                if (canPlace(board, row, column, turn, false)) {
                    board[row][column] = CAN_PLACE;
                    hasSquareToPlace = true;
                }
            }
        }
        return hasSquareToPlace;
    }

    /**
     * 石を置けるか判定+replaceがtrueなら石をおいてひっくり返す
     *
     * @param board   盤面
     * @param row     石を置く行(1-8)
     * @param column  石を置く列(a-h)
     * @param turn    手番
     * @param replace ひっくり返すか
     * @return 石を置けるか
     */
    public static boolean canPlace(int[][] board, int row, int column, int turn, boolean replace) {
        if (board[row][column] == BLACK || board[row][column] == WHITE) {
            return false;
        }
        final int OPPOSITE = changeTurn(turn);
        boolean canPlace = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    boolean isPreviousOpposite = false;
                    int distance;
                    for (distance = 1; ; distance++) {
                        if (row + i * distance < 0 || column + j * distance < 0 ||
                                row + i * distance >= BOARD_SIZE || column + j * distance >= BOARD_SIZE) {
                            distance = 0;
                            break;
                        }
                        if (board[row + i * distance][column + j * distance] != OPPOSITE && (!isPreviousOpposite || board[row + i * distance][column + j * distance] != turn)) {
                            distance = 0;
                            break;
                        }
                        if (board[row + i * distance][column + j * distance] == turn && isPreviousOpposite) {
                            canPlace = true;
                            break;
                        }
                        isPreviousOpposite = true;
                    }
                    if (replace) {
                        for (int k = 1; k < distance; k++) {
                            board[row + i * k][column + j * k] = turn;
                        }
                    }
                }
            }
        }
        if (replace && canPlace) {
            board[row][column] = turn;
        }
        return canPlace;
    }

    public static void draw(int[][] board) {
        if (board.length != BOARD_SIZE || board[0].length != BOARD_SIZE) {
            throw new ArrayIndexOutOfBoundsException();
        }
        System.out.print(" ");
        for (int column = 0; column < BOARD_SIZE; column++) {
            System.out.print(" " + (char) ('a' + column));
        }
        System.out.println();
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.print(row + 1);
            for (int square : board[row]) {
                System.out.print(" ");
                if (square == BLACK) {
                    System.out.print("x");
                } else if (square == WHITE) {
                    System.out.print("o");
                } else if (square == CAN_PLACE) {
                    System.out.print(".");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    public static int changeTurn(int turn) {
        if (turn == BLACK) {
            return WHITE;
        } else if (turn == WHITE) {
            return BLACK;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
