package org.lichess.compression.game;

import org.lichess.compression.BitReader;
import org.lichess.compression.BitWriter;

class Huffman {
    public static void write(int value, BitWriter writer) {
        Symbol symbol = CODES[value];
        writer.writeBits(symbol.code, symbol.bits);
    }

    public static int read(BitReader reader) {
        Node node = ROOT;
        while (node.zero != null && node.one != null) {
            int bit = reader.readBits(1);
            if (bit == 0) node = node.zero;
            else node = node.one;
        }
        return node.leaf;
    }

    private static class Symbol {
        public final int code;
        public final int bits;

        public Symbol(int code, int bits) {
            this.code = code;
            this.bits = bits;
        }
    }

    private static class Node {
        public final Node zero;
        public final Node one;
        public final int leaf;

        public Node(int leaf) {
            this.zero = null;
            this.one = null;
            this.leaf = leaf;
        }

        public Node(Node zero, Node one) {
            this.zero = zero;
            this.one = one;
            this.leaf = -1;
        }
    }

    private static Node buildTree(int code, int bits) {
        assert bits <= 32;

        for (int i = 0; i <= 0xff; i++) {
            if (CODES[i].code == code && CODES[i].bits == bits) {
                return new Node(i);
            }
        }

        return new Node(
            buildTree(code << 1, bits + 1),
            buildTree((code << 1) | 1, bits + 1));
    }

    // Huffman code for indexes in the legal move list. Precomputed based on
    // actual frequency in 1,048,440 rated games.
    //
    // This is based on a maximum of 256 legal moves per position, but the
    // highest indexes did not actually occur. They were manually assigned a
    // frequency of 1 and ordered.
    //
    // On the training corpus this achieves:
    // 37.33 bytes per game
    // 0.555 bytes per move
    private static final Symbol CODES[] = {
        new Symbol(0b00, 2), // 0: 14270036
        new Symbol(0b100, 3), // 1: 8545189
        new Symbol(0b1101, 4), // 2: 5681945
        new Symbol(0b1010, 4), // 3: 4500515
        new Symbol(0b0101, 4), // 4: 3716810
        new Symbol(0b11101, 5), // 5: 2935984
        new Symbol(0b10111, 5), // 6: 2407106
        new Symbol(0b01110, 5), // 7: 2078074
        new Symbol(0b01100, 5), // 8: 1873563
        new Symbol(0b01000, 5), // 9: 1749200
        new Symbol(0b111101, 6), // 10: 1615208
        new Symbol(0b111001, 6), // 11: 1519343
        new Symbol(0b111100, 6), // 12: 1535645
        new Symbol(0b110011, 6), // 13: 1352575
        new Symbol(0b110010, 6), // 14: 1341473
        new Symbol(0b110000, 6), // 15: 1211427
        new Symbol(0b101101, 6), // 16: 1144034
        new Symbol(0b101100, 6), // 17: 1123878
        new Symbol(0b011111, 6), // 18: 1087002
        new Symbol(0b011011, 6), // 19: 1013522
        new Symbol(0b010011, 6), // 20: 945343
        new Symbol(0b011010, 6), // 21: 948110
        new Symbol(0b1111111, 7), // 22: 883669
        new Symbol(0b1111101, 7), // 23: 853114
        new Symbol(0b1111110, 7), // 24: 862404
        new Symbol(0b1111100, 7), // 25: 789435
        new Symbol(0b1110000, 7), // 26: 702903
        new Symbol(0b1100011, 7), // 27: 670700
        new Symbol(0b0111101, 7), // 28: 547931
        new Symbol(0b0111100, 7), // 29: 480012
        new Symbol(0b0100100, 7), // 30: 413962
        new Symbol(0b11100010, 8), // 31: 348907
        new Symbol(0b11000101, 8), // 32: 295987
        new Symbol(0b01001011, 8), // 33: 253504
        new Symbol(0b111000111, 9), // 34: 197578
        new Symbol(0b110001001, 9), // 35: 161323
        new Symbol(0b010010101, 9), // 36: 124019
        new Symbol(0b010010100, 9), // 37: 97303
        new Symbol(0b1110001100, 10), // 38: 75240
        new Symbol(0b1100010000, 10), // 39: 59951
        new Symbol(0b11100011010, 11), // 40: 42572
        new Symbol(0b11000100010, 11), // 41: 32445
        new Symbol(0b111000110110, 12), // 42: 23548
        new Symbol(0b110001000110, 12), // 43: 17325
        new Symbol(0b1110001101110, 13), // 44: 12392
        new Symbol(0b1100010001110, 13), // 45: 8790
        new Symbol(0b11100011011110, 14), // 46: 6305
        new Symbol(0b11000100011110, 14), // 47: 4453
        new Symbol(0b111000110111110, 15), // 48: 3031
        new Symbol(0b110001000111110, 15), // 49: 2153
        new Symbol(0b1110001101111110, 16), // 50: 1542
        new Symbol(0b1100010001111110, 16), // 51: 1083
        new Symbol(0b11100011011111110, 17), // 52: 783
        new Symbol(0b111000110111111111, 18), // 53: 531
        new Symbol(0b110001000111111110, 18), // 54: 337
        new Symbol(0b110001000111111100, 18), // 55: 252
        new Symbol(0b1100010001111111111, 19), // 56: 204
        new Symbol(0b1100010001111111010, 19), // 57: 140
        new Symbol(0b11000100011111111101, 20), // 58: 90
        new Symbol(0b11000100011111110111, 20), // 59: 74
        new Symbol(0b111000110111111110010, 21), // 60: 52
        new Symbol(0b111000110111111110100, 21), // 61: 59
        new Symbol(0b111000110111111110000, 21), // 62: 46
        new Symbol(0b110001000111111101101, 21), // 63: 36
        new Symbol(0b1110001101111111100111, 22), // 64: 28
        new Symbol(0b1100010001111111110010, 22), // 65: 20
        new Symbol(0b1110001101111111100011, 22), // 66: 24
        new Symbol(0b1100010001111111011000, 22), // 67: 12
        new Symbol(0b11100011011111111001100, 23), // 68: 15
        new Symbol(0b11100011011111111011110, 23), // 69: 16
        new Symbol(0b11000100011111110110011, 23), // 70: 8
        new Symbol(0b11100011011111111011111, 23), // 71: 16
        new Symbol(0b11000100011111111100111, 23), // 72: 6
        new Symbol(0b11100011011111111000100, 23), // 73: 11
        new Symbol(0b11000100011111111100000, 23), // 74: 11
        new Symbol(0b111000110111111110011010, 24), // 75: 4
        new Symbol(0b110001000111111111000110, 24), // 76: 6
        new Symbol(0b111000110111111110001011, 24), // 77: 4
        new Symbol(0b110001000111111111000101, 24), // 78
        new Symbol(0b110001000111111111000011, 24), // 79: 9
        new Symbol(0b110001000111111111000100, 24), // 80: 4
        new Symbol(0b110001000111111111000010, 24), // 81: 4
        new Symbol(0b110001000111111111000111, 24), // 82: 3
        new Symbol(0b1110001101111111101101110, 25), // 83: 4
        new Symbol(0b1100010001111111110011011, 25), // 84: 2
        new Symbol(0b1100010001111111110011010, 25), // 85: 2
        new Symbol(0b1100010001111111110011000, 25), // 86: 2
        new Symbol(0b1110001101111111100010100, 25), // 87: 2
        new Symbol(0b1110001101111111100010101, 25), // 88: 1
        new Symbol(0b1110001101111111101110000, 25), // 89: 2
        new Symbol(0b11100011011111111011010000, 26), // 90: 4
        new Symbol(0b11100011011111111010100101, 26), // 91: 1
        new Symbol(0b11100011011111111010100100, 26), // 92
        new Symbol(0b11100011011111111011101001, 26), // 93
        new Symbol(0b11100011011111111011100111, 26), // 94: 3
        new Symbol(0b11000100011111110110010101, 26), // 95: 1
        new Symbol(0b11000100011111110110010010, 26), // 96
        new Symbol(0b11000100011111110110010111, 26), // 97: 1
        new Symbol(0b11000100011111110110010110, 26), // 98
        new Symbol(0b11000100011111110110010100, 26), // 99
        new Symbol(0b11000100011111110110010011, 26), // 100: 1
        new Symbol(0b11000100011111110110010001, 26), // 101
        new Symbol(0b11000100011111110110010000, 26), // 102
        new Symbol(0b11000100011111111100110010, 26), // 103
        new Symbol(0b111000110111111110110000001, 27), // 104
        new Symbol(0b111000110111111110101000000, 27), // 105
        new Symbol(0b111000110111111110101101101, 27), // 106
        new Symbol(0b111000110111111110101110010, 27), // 107
        new Symbol(0b111000110111111110110010101, 27), // 108
        new Symbol(0b111000110111111110111010100, 27), // 109
        new Symbol(0b111000110111111110110010001, 27), // 110
        new Symbol(0b111000110111111110101000010, 27), // 111
        new Symbol(0b111000110111111110111001101, 27), // 112
        new Symbol(0b111000110111111110111000110, 27), // 113
        new Symbol(0b111000110111111110111000101, 27), // 114
        new Symbol(0b111000110111111110110010110, 27), // 115
        new Symbol(0b111000110111111110110010011, 27), // 116
        new Symbol(0b111000110111111110110011100, 27), // 117
        new Symbol(0b111000110111111110101010101, 27), // 118
        new Symbol(0b111000110111111110101101000, 27), // 119
        new Symbol(0b111000110111111110101011001, 27), // 120
        new Symbol(0b111000110111111110101010000, 27), // 121
        new Symbol(0b111000110111111110101111011, 27), // 122
        new Symbol(0b111000110111111110101010110, 27), // 123
        new Symbol(0b111000110111111110101010011, 27), // 124
        new Symbol(0b111000110111111110011011100, 27), // 125
        new Symbol(0b111000110111111110011011011, 27), // 126
        new Symbol(0b111000110111111110101111000, 27), // 127
        new Symbol(0b111000110111111110110111101, 27), // 128
        new Symbol(0b111000110111111110110001100, 27), // 129
        new Symbol(0b111000110111111110110001110, 27), // 130
        new Symbol(0b111000110111111110110001001, 27), // 131
        new Symbol(0b111000110111111110101110110, 27), // 132
        new Symbol(0b111000110111111110101110000, 27), // 133
        new Symbol(0b110001000111111111001100111, 27), // 134
        new Symbol(0b111000110111111110101110101, 27), // 135
        new Symbol(0b111000110111111110101110100, 27), // 136
        new Symbol(0b111000110111111110110101001, 27), // 137
        new Symbol(0b111000110111111110110101000, 27), // 138
        new Symbol(0b111000110111111110101100111, 27), // 139
        new Symbol(0b111000110111111110101100110, 27), // 140
        new Symbol(0b111000110111111110110100111, 27), // 141
        new Symbol(0b111000110111111110110100110, 27), // 142
        new Symbol(0b111000110111111110101100101, 27), // 143
        new Symbol(0b111000110111111110101100100, 27), // 144
        new Symbol(0b111000110111111110110000111, 27), // 145
        new Symbol(0b111000110111111110110000110, 27), // 146
        new Symbol(0b111000110111111110110000101, 27), // 147
        new Symbol(0b111000110111111110110000100, 27), // 148
        new Symbol(0b111000110111111110110100011, 27), // 149
        new Symbol(0b111000110111111110110100010, 27), // 150
        new Symbol(0b111000110111111110101100011, 27), // 151
        new Symbol(0b111000110111111110101100010, 27), // 152
        new Symbol(0b111000110111111110111011111, 27), // 153
        new Symbol(0b111000110111111110111011110, 27), // 154
        new Symbol(0b111000110111111110111011101, 27), // 155
        new Symbol(0b111000110111111110111011100, 27), // 156
        new Symbol(0b111000110111111110111011011, 27), // 157
        new Symbol(0b111000110111111110111011010, 27), // 158
        new Symbol(0b111000110111111110101100001, 27), // 159
        new Symbol(0b111000110111111110101100000, 27), // 160
        new Symbol(0b111000110111111110111011001, 27), // 161
        new Symbol(0b111000110111111110111011000, 27), // 162
        new Symbol(0b111000110111111110110000011, 27), // 163
        new Symbol(0b111000110111111110110000010, 27), // 164
        new Symbol(0b111000110111111110110000000, 27), // 165
        new Symbol(0b111000110111111110110110111, 27), // 166
        new Symbol(0b111000110111111110110110110, 27), // 167
        new Symbol(0b111000110111111110110110101, 27), // 168
        new Symbol(0b111000110111111110110110100, 27), // 169
        new Symbol(0b111000110111111110110101101, 27), // 170
        new Symbol(0b111000110111111110110101100, 27), // 171
        new Symbol(0b111000110111111110110110011, 27), // 172
        new Symbol(0b111000110111111110110110010, 27), // 173
        new Symbol(0b111000110111111110110110001, 27), // 174
        new Symbol(0b111000110111111110110110000, 27), // 175
        new Symbol(0b111000110111111110110011011, 27), // 176
        new Symbol(0b111000110111111110110011010, 27), // 177
        new Symbol(0b111000110111111110110101111, 27), // 178
        new Symbol(0b111000110111111110110101110, 27), // 179
        new Symbol(0b111000110111111110110011001, 27), // 180
        new Symbol(0b111000110111111110110011000, 27), // 181
        new Symbol(0b111000110111111110110101011, 27), // 182
        new Symbol(0b111000110111111110110101010, 27), // 183
        new Symbol(0b111000110111111110101000111, 27), // 184
        new Symbol(0b111000110111111110101000110, 27), // 185
        new Symbol(0b111000110111111110101001111, 27), // 186
        new Symbol(0b111000110111111110101001110, 27), // 187
        new Symbol(0b111000110111111110101000101, 27), // 188
        new Symbol(0b111000110111111110101000100, 27), // 189
        new Symbol(0b111000110111111110110100101, 27), // 190
        new Symbol(0b111000110111111110110100100, 27), // 191
        new Symbol(0b111000110111111110101000001, 27), // 192
        new Symbol(0b111000110111111110101101111, 27), // 193
        new Symbol(0b111000110111111110101101110, 27), // 194
        new Symbol(0b111000110111111110101101100, 27), // 195
        new Symbol(0b111000110111111110101001101, 27), // 196
        new Symbol(0b111000110111111110101001100, 27), // 197
        new Symbol(0b111000110111111110111001011, 27), // 198
        new Symbol(0b111000110111111110111001010, 27), // 199
        new Symbol(0b111000110111111110101110011, 27), // 200
        new Symbol(0b111000110111111110111001001, 27), // 201
        new Symbol(0b111000110111111110111001000, 27), // 202
        new Symbol(0b111000110111111110111010111, 27), // 203
        new Symbol(0b111000110111111110111010110, 27), // 204
        new Symbol(0b111000110111111110110010100, 27), // 205
        new Symbol(0b111000110111111110111010101, 27), // 206
        new Symbol(0b111000110111111110101011111, 27), // 207
        new Symbol(0b111000110111111110101011110, 27), // 208
        new Symbol(0b111000110111111110111010001, 27), // 209
        new Symbol(0b111000110111111110111010000, 27), // 210
        new Symbol(0b111000110111111110110010000, 27), // 211
        new Symbol(0b111000110111111110101000011, 27), // 212
        new Symbol(0b111000110111111110101101011, 27), // 213
        new Symbol(0b111000110111111110101101010, 27), // 214
        new Symbol(0b111000110111111110111001100, 27), // 215
        new Symbol(0b111000110111111110111000111, 27), // 216
        new Symbol(0b111000110111111110101011101, 27), // 217
        new Symbol(0b111000110111111110101011100, 27), // 218
        new Symbol(0b111000110111111110111000100, 27), // 219
        new Symbol(0b111000110111111110110010111, 27), // 220
        new Symbol(0b111000110111111110110011111, 27), // 221
        new Symbol(0b111000110111111110110011110, 27), // 222
        new Symbol(0b111000110111111110110010010, 27), // 223
        new Symbol(0b111000110111111110110011101, 27), // 224
        new Symbol(0b111000110111111110101011011, 27), // 225
        new Symbol(0b111000110111111110101011010, 27), // 226
        new Symbol(0b111000110111111110101010100, 27), // 227
        new Symbol(0b111000110111111110101101001, 27), // 228
        new Symbol(0b111000110111111110101111111, 27), // 229
        new Symbol(0b111000110111111110101111110, 27), // 230
        new Symbol(0b111000110111111110101011000, 27), // 231
        new Symbol(0b111000110111111110101010001, 27), // 232
        new Symbol(0b111000110111111110101111101, 27), // 233
        new Symbol(0b111000110111111110101111100, 27), // 234
        new Symbol(0b111000110111111110101111010, 27), // 235
        new Symbol(0b111000110111111110101010111, 27), // 236
        new Symbol(0b111000110111111110011011111, 27), // 237
        new Symbol(0b111000110111111110011011110, 27), // 238
        new Symbol(0b111000110111111110101010010, 27), // 239
        new Symbol(0b111000110111111110011011101, 27), // 240
        new Symbol(0b111000110111111110110111111, 27), // 241
        new Symbol(0b111000110111111110110111110, 27), // 242
        new Symbol(0b111000110111111110011011010, 27), // 243
        new Symbol(0b111000110111111110101111001, 27), // 244
        new Symbol(0b111000110111111110011011001, 27), // 245
        new Symbol(0b111000110111111110011011000, 27), // 246
        new Symbol(0b111000110111111110110111100, 27), // 247
        new Symbol(0b111000110111111110110001101, 27), // 248
        new Symbol(0b111000110111111110110001111, 27), // 249
        new Symbol(0b111000110111111110110001011, 27), // 250
        new Symbol(0b111000110111111110110001010, 27), // 251
        new Symbol(0b111000110111111110110001000, 27), // 252
        new Symbol(0b111000110111111110101110111, 27), // 253
        new Symbol(0b111000110111111110101110001, 27), // 254
        new Symbol(0b110001000111111111001100110, 27), // 255
    };

    private static final Node ROOT = buildTree(0, 0);
}
